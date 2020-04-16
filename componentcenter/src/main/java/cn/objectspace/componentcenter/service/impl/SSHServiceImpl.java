package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.URPDto;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.*;
import cn.objectspace.componentcenter.pojo.entity.CloudServerCommandExecuteRecord;
import cn.objectspace.componentcenter.pojo.entity.SimpleCommand;
import cn.objectspace.componentcenter.service.SSHService;
import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: ssh命令
 * @Author: NoCortY
 * @Date: 2020/4/14
 */
@Service
public class SSHServiceImpl implements SSHService {
    @Autowired
    private ComponentDao componentDao;
    @Autowired
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(SSHServiceImpl.class);

    private static Map<String, SessionLease> sessionMap = new ConcurrentHashMap<>();

    /**
     * @Description: 初始化连接
     * @Param: [webSSHDataDto]
     * @return: com.jcraft.jsch.Session
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    @Override
    public Session initConnection(String userId, WebSSHDataDto webSSHDataDto) throws Exception {
        ServerSSHInfoDto serverSSHInfoDto = componentDao.queryServerSSHInfoByUserIdAndServerIp(Integer.valueOf(userId), webSSHDataDto.getHost());
        if (serverSSHInfoDto == null) {
            logger.info("该用户没有该服务器的控制信息");
            return null;
        }
        logger.info("exec连接:IP:{},用户名:{}", webSSHDataDto.getHost(), serverSSHInfoDto.getSshUser());
        long l = System.currentTimeMillis();
        String sessionCacheKey = userId + ":" + webSSHDataDto.getHost();

        //懒淘汰策略
        //如果缓存中有，直接从缓存中返回
        if (sessionMap.containsKey(sessionCacheKey)) {
            SessionLease sessionLease = sessionMap.get(sessionCacheKey);
            //首先检查是否过期
            if (System.currentTimeMillis() <= sessionLease.getExpireTimeMillis()) {
                //如果没有过期，那么就返回，同时给这个缓存中的过期时间续约
                sessionLease.setExpireTimeMillis(System.currentTimeMillis() + 30 * 60 * 1000);
                return sessionLease.getSession();
            } else {
                //断开连接
                sessionMap.get(sessionCacheKey).getSession().disconnect();
                //如果过期了，就从缓存中删除，然后设置新的连接
                sessionMap.remove(sessionCacheKey);
            }

        }
        JSch jsch = new JSch();
        Session session = jsch.getSession(serverSSHInfoDto.getSshUser(), webSSHDataDto.getHost());
        if (session == null) {
            logger.error("无法连接该服务器");
            throw new Exception("无法连接该服务器");
        }
        //设置密码
        session.setPassword(serverSSHInfoDto.getSshPassword());

        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
//
//        //设置登陆超时时间
        session.connect(30000);
        //加入缓存
        SessionLease sessionLease = new SessionLease();
        //过期时间为30min
        sessionLease.setExpireTimeMillis(System.currentTimeMillis() + 10 * 60 * 1000);
        sessionLease.setSession(session);
        sessionMap.put(sessionCacheKey, sessionLease);
        //返回session
        return session;
    }


    /**
     * @Description: 命令群发
     * @Param: [session, servers, command]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    @Override
    public List<ExecuteCommandReturnDto> groupCommand(List<Session> sessions, String command, Integer userId) {
        if (sessions == null || StringUtils.isBlank(command) || userId == null) {
            logger.info("命令群发有部分入参缺失");
            return null;
        }
        //执行反馈集合
        List<ExecuteCommandReturnDto> executeCommandReturnDtos = new LinkedList<>();
        //执行记录集合
        List<CloudServerCommandExecuteRecord> cloudServerCommandExecuteRecords = new LinkedList<>();
        for (Session session : sessions) {
            ExecuteCommandReturnDto executeCommandReturnDto = new ExecuteCommandReturnDto();
            CloudServerCommandExecuteRecord cloudServerCommandExecuteRecord = new CloudServerCommandExecuteRecord();
            Channel channel = null;
            //打开exec通道
            try {
                channel = session.openChannel("exec");

            } catch (JSchException e) {
                logger.error("exec channel创建失败");
                logger.error("异常信息:{}", e.getMessage());
                executeCommandReturnDto.setServerIp(session.getHost());
                executeCommandReturnDto.setCommand(command);
                executeCommandReturnDto.setResult("命令执行失败");
                executeCommandReturnDtos.add(executeCommandReturnDto);
                continue;
            }

            ChannelExec channelExec = (ChannelExec) channel;

            //获取通道输入流,从linux端到达的数据，在这个流中都能读到
            InputStream inputStream = null;
            try {
                inputStream = channelExec.getInputStream();
            } catch (IOException e) {
                logger.error("获取输入流异常");
                logger.error("异常信息:{}", e.getMessage());
                executeCommandReturnDto.setServerIp(session.getHost());
                executeCommandReturnDto.setCommand(command);
                executeCommandReturnDto.setResult("命令执行失败");
                executeCommandReturnDtos.add(executeCommandReturnDto);
                continue;
            }
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);
            try {
                channelExec.connect();
            } catch (JSchException e) {
                logger.error("通道连接失败");
                logger.error("异常信息:{}", e.getMessage());
                executeCommandReturnDto.setServerIp(session.getHost());
                executeCommandReturnDto.setCommand(command);
                executeCommandReturnDto.setResult("命令执行失败");
                executeCommandReturnDtos.add(executeCommandReturnDto);
                continue;
            }
            try {
                executeCommandReturnDto.setServerIp(session.getHost());
                executeCommandReturnDto.setCommand(command);
                //构建执行反馈对象
                //换行符转换为前端的换行符
                String result = IOUtils.toString(inputStream, "utf-8");
                executeCommandReturnDto.setResult(result.replace("\n", "<br>"));
                executeCommandReturnDtos.add(executeCommandReturnDto);
                //构建服务器运行记录对象
                cloudServerCommandExecuteRecord.setCommand(command);
                Date date = new Date();
                Timestamp timeStamp = new Timestamp(date.getTime());
                cloudServerCommandExecuteRecord.setExecuteDate(timeStamp);

                cloudServerCommandExecuteRecord.setResult(result);
                cloudServerCommandExecuteRecord.setServerIp(session.getHost());
                URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + userId)));
                if (urpDto != null) {
                    cloudServerCommandExecuteRecord.setExecuteUserId(urpDto.getUserId());
                    cloudServerCommandExecuteRecord.setExecuteUserName(urpDto.getUserName());
                    cloudServerCommandExecuteRecord.setExecuteUserEmail(urpDto.getUserEmail());
                } else {
                    throw new Exception("权限不足");
                }
                cloudServerCommandExecuteRecords.add(cloudServerCommandExecuteRecord);
            } catch (Exception e) {
                logger.error("获取运行结果异常");
                logger.info("异常信息:{}", e.getMessage());
                executeCommandReturnDto.setServerIp(session.getHost());
                executeCommandReturnDto.setCommand(command);
                executeCommandReturnDto.setResult("命令执行失败");
                executeCommandReturnDtos.add(executeCommandReturnDto);
            } finally {
                channelExec.disconnect();
                channel.disconnect();
            }
        }
        //进行命令执行数据一次性入库
        componentDao.insertExecuteCommandRecord(cloudServerCommandExecuteRecords);
        return executeCommandReturnDtos;
    }

    @Override
    public List<CloudServerCommandExecuteRecordDto> getServerCommandExecuteRecord(Integer userId) {
        if (userId == null) {
            logger.info("用户id不能为空");
            return null;
        }
        List<CloudServerCommandExecuteRecordDto> cloudServerCommandExecuteRecordDtoList = componentDao.queryServerCommandExecuteRecordByUserId(userId);
        for (CloudServerCommandExecuteRecordDto cloudServerCommandExecuteRecordDto : cloudServerCommandExecuteRecordDtoList) {
            String replace = cloudServerCommandExecuteRecordDto.getResult().replace("\n", "<br>");
            cloudServerCommandExecuteRecordDto.setResult(replace);
        }
        return cloudServerCommandExecuteRecordDtoList;
    }

    @Override
    public List<SimpleCommandDto> getSimpleCommands(Integer userId) {
        if (userId == null) {
            logger.info("用户id不能为空");
            return null;
        }
        return componentDao.querySimpleCommandList(userId);
    }

    /**
     * @Description: 新增常用命令
     * @Param: [commandName, commandContent, userId]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    @Override
    public boolean addSimpleCommand(String commandName, String commandContent, Integer userId) {
        SimpleCommand simpleCommand = new SimpleCommand();
        URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + userId)));
        //urpDto没了说明已经失去了权限
        if (urpDto == null) return false;
        simpleCommand.setCommandName(commandName);
        simpleCommand.setCommandContent(commandContent);
        simpleCommand.setCreateUserId(userId);
        simpleCommand.setCreateUserEmail(urpDto.getUserEmail());
        simpleCommand.setCreateUserName(urpDto.getUserName());
        Integer effectiveNum = componentDao.insertSimpleCommand(simpleCommand);
        return effectiveNum > 0;
    }

    @Override
    public boolean removeSimpleCommand(Integer commandId, Integer userId) {
        if (commandId == null || userId == null) {
            logger.info("id不能为空");
            return false;
        }
        return componentDao.deleteSimpleCommandById(commandId, userId) > 0;
    }

}
