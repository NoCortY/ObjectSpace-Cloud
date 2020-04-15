package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.ServerSSHInfoDto;
import cn.objectspace.componentcenter.pojo.dto.SessionLease;
import cn.objectspace.componentcenter.pojo.dto.WebSSHDataDto;
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
import java.util.HashMap;
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
    public Map<String, String> groupCommand(List<Session> sessions, String command) {
        if (sessions == null || StringUtils.isBlank(command)) {
            logger.info("命令群发有部分入参缺失");
            return null;
        }
        Map<String, String> res = new HashMap<>();
        for (Session session : sessions) {
            Channel channel = null;
            //打开exec通道
            try {
                channel = session.openChannel("exec");

            } catch (JSchException e) {
                logger.error("exec channel创建失败");
                logger.error("异常信息:{}", e.getMessage());
                res.put(session.getHost(), "命令发送失败");
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
                res.put(session.getHost(), "命令发送失败");
                continue;
            }
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);
            try {
                channelExec.connect();
            } catch (JSchException e) {
                logger.error("通道连接失败");
                logger.error("异常信息:{}", e.getMessage());
                res.put(session.getHost(), "命令发送失败");
                continue;
            }
            try {
                res.put(session.getHost(), IOUtils.toString(inputStream, "utf-8"));
            } catch (IOException e) {
                logger.error("获取运行结果异常");
                logger.info("异常信息:{}", e.getMessage());
                res.put(session.getHost(), "命令发送失败");
            } finally {
                channelExec.disconnect();
                channel.disconnect();
            }
        }

        return res;
    }

}
