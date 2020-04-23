package cn.objectspace.componentcenter.scheduled;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.URPDto;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.componentcenter.controller.websocket.ServerResumeWebSocketHandler;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.ServerResumeDto;
import cn.objectspace.componentcenter.pojo.dto.ServerSSHInfoDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.FutureTask;
import cn.objectspace.componentcenter.pojo.entity.ServerRuntimeRecord;
import cn.objectspace.componentcenter.service.MailService;
import cn.objectspace.componentcenter.service.ServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 定时任务：服务器巡检
 * @Author: NoCortY
 * @Date: 2020/3/2
 */
@Component
@EnableScheduling
@EnableAsync
public class ServerPatrol {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ServerService serverService;

    @Autowired
    private MailService mailService;
    private static Logger logger = LoggerFactory.getLogger(ServerPatrol.class);

    //线程池
    private static ExecutorService threadPool = Executors.newCachedThreadPool();


    /**
     * @Description: 巡检服务器在线状态，如果出现异常状态会发送邮件提醒服务器owner
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/2
     */
    @Async
    @Scheduled(cron = "0/30 * * * * ? ")//0s开始执行，每30s执行一次
    public void updateServerStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("=======服务器巡检开始 开始时间:{}", sdf.format(new Date()) + "=======");
        Set<String> serverUsers = redisUtil.hkeys(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP);
        List<CloudServer> serverInfoDtos = null;
        if (serverUsers == null || serverUsers.isEmpty()) {
            //如果取出来的为空，那么说明有可能redis宕机了，为了保险起见，去数据库查。
            serverInfoDtos = componentDao.queryRegisteredServer();
            for (CloudServer s : serverInfoDtos) {
                String key = s.getServerUser() + ":" + s.getServerIp();
                byte[] bytes = redisUtil.get(SerializeUtil.serialize(key));
                List<String> serverStatus = redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, key);
                if (bytes == null || bytes.length <= 0) {
                    //说明已经离线了
                    if (ConstantPool.ComponentCenter.SERVER_ONLINE.equals(serverStatus.get(0))) {
                        //如果之前的状态是在线,那么需要发送邮件通知
                        //获取该用户邮箱
                        URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + s.getServerUser())));
                        if (urpDto != null) {
                            //发送邮件使用异步
                            //使用线程池
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mailService.sendSimpleMail(urpDto.getUserEmail(), "ObjectSpace自动化运维平台-服务器离线通知", "您的服务器 IP:" + s.getServerIp() + " 已停机，请及时处理。");
                                    logger.info("===邮件发送至:{},结束===", urpDto.getUserEmail());
                                }
                            });

                        } else {
                            //为空
                            logger.info("用户缓存为空");
                        }

                    }
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, key, ConstantPool.ComponentCenter.SERVER_OFFLINE);
                } else {
                    //获取上一次服务器状态
                    if (ConstantPool.ComponentCenter.SERVER_OFFLINE.equals(serverStatus.get(0))) {
                        //如果原来是离线状态，现在恢复了在线，那么就发送邮件提示服务器已正常上线，进入监控状态
                        //获取用户id
                        String userId = String.valueOf(s.getServerUser());
                        //获取服务器IP
                        String serverIp = s.getServerIp();
                        //获取该用户邮箱
                        URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + userId)));
                        if (urpDto != null) {
                            //如果缓存不为空则直接获取邮箱
                            final String userEmail = urpDto.getUserEmail();
                            //发送邮件开启线程，使用异步
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    ServerRuntimeRecord record = new ServerRuntimeRecord();
                                    logger.info("============启动事件入库============");

                                    Date now = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    record.setServerIp(serverIp);
                                    record.setUserId(Integer.valueOf(userId));
                                    record.setUserEmail(userEmail);
                                    record.setRecordTime(now);
                                    record.setRecordContent("您的服务器:" + serverIp + "于" + sdf.format(now) + "正常启动，进入监控状态。");
                                    record.setServerState(1);

                                    int effectiveNum = componentDao.insertServerRuntimeRecord(record);
                                    if (effectiveNum > 0) {
                                        logger.info("============事件入库成功============");
                                    } else {
                                        logger.info("============事件入库失败============");
                                    }
                                    try {
                                        mailService.sendSimpleMail(userEmail, "ObjectSpace自动化运维平台-服务器上线通知", "您的服务器 IP:" + serverIp + " 已正常启动，进入监控状态。");
                                    } catch (Exception e) {
                                        logger.error("发送邮件异常");
                                        logger.error("异常信息:{}", e.getMessage());
                                    }
                                    logger.info("邮件发送至:{},结束", userEmail);
                                }
                            });
                        } else {
                            //为空
                            logger.info("用户缓存为空");
                        }
                    }
                    //说明在线
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, key, ConstantPool.ComponentCenter.SERVER_ONLINE);
                }
            }
        } else {
            //如果不为空，说明redis没有宕机，即使数据与数据库中不相同，发送心跳时也会同步
            for (String serverUser : serverUsers) {
                byte[] bytes = redisUtil.get(SerializeUtil.serialize(serverUser));
                List<String> serverStatus = redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, serverUser);
                if (bytes == null || bytes.length <= 0) {
                    //获取上一次服务器状态
                    if (ConstantPool.ComponentCenter.SERVER_ONLINE.equals(serverStatus.get(0))) {
                        //如果之前的状态是在线,那么需要发送邮件通知
                        //获取用户id
                        String userId = serverUser.split(":")[0];
                        //获取服务器IP
                        String serverIp = serverUser.split(":")[1];
                        //获取该用户邮箱
                        URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + userId)));
                        if (urpDto != null) {
                            //如果缓存不为空则直接获取邮箱
                            final String userEmail = urpDto.getUserEmail();
                            //发送邮件开启线程，使用异步
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    ServerRuntimeRecord record = new ServerRuntimeRecord();
                                    logger.info("============宕机事件入库============");

                                    Date now = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    record.setServerIp(serverIp);
                                    record.setUserId(Integer.valueOf(userId));
                                    record.setUserEmail(userEmail);
                                    record.setRecordTime(now);
                                    record.setRecordContent("您的服务器:" + serverIp + "于" + sdf.format(now) + "宕机，请及时处置。");
                                    record.setServerState(2);

                                    int effectiveNum = componentDao.insertServerRuntimeRecord(record);
                                    if (effectiveNum > 0) {
                                        logger.info("============事件入库成功============");
                                    } else {
                                        logger.info("============事件入库失败============");
                                    }
                                    try {
                                        mailService.sendSimpleMail(userEmail, "ObjectSpace自动化运维平台-服务器离线通知", "您的服务器 IP:" + serverIp + " 已停机，请及时处理。");
                                    } catch (Exception e) {
                                        logger.error("发送邮件异常");
                                        logger.error("异常信息:{}", e.getMessage());
                                    }
                                    logger.info("邮件发送至:{},结束", userEmail);
                                }
                            });
                        } else {
                            //为空
                            logger.info("用户缓存为空");
                        }

                    }
                    //离线
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, serverUser, ConstantPool.ComponentCenter.SERVER_OFFLINE);
                } else {
                    //获取上一次服务器状态
                    if (ConstantPool.ComponentCenter.SERVER_OFFLINE.equals(serverStatus.get(0))) {
                        //如果原来是离线状态，现在恢复了在线，那么就发送邮件提示服务器已正常上线，进入监控状态
                        //获取用户id
                        String userId = serverUser.split(":")[0];
                        //获取服务器IP
                        String serverIp = serverUser.split(":")[1];
                        //获取该用户邮箱
                        URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + userId)));
                        if (urpDto != null) {
                            //如果缓存不为空则直接获取邮箱
                            final String userEmail = urpDto.getUserEmail();
                            //发送邮件开启线程，使用异步
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    ServerRuntimeRecord record = new ServerRuntimeRecord();
                                    logger.info("============启动事件入库============");

                                    Date now = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    record.setServerIp(serverIp);
                                    record.setUserId(Integer.valueOf(userId));
                                    record.setUserEmail(userEmail);
                                    record.setRecordTime(now);
                                    record.setRecordContent("您的服务器:" + serverIp + "于" + sdf.format(now) + "正常启动，进入监控状态。");
                                    record.setServerState(1);

                                    int effectiveNum = componentDao.insertServerRuntimeRecord(record);
                                    if (effectiveNum > 0) {
                                        logger.info("============事件入库成功============");
                                    } else {
                                        logger.info("============事件入库失败============");
                                    }
                                    try {
                                        mailService.sendSimpleMail(userEmail, "ObjectSpace自动化运维平台-服务器上线通知", "您的服务器 IP:" + serverIp + " 已正常启动，进入监控状态。");
                                    } catch (Exception e) {
                                        logger.error("发送邮件异常");
                                        logger.error("异常信息:{}", e.getMessage());
                                    }
                                    logger.info("邮件发送至:{},结束", userEmail);
                                }
                            });
                        } else {
                            //为空
                            logger.info("用户缓存为空");
                        }
                    }
                    //在线
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, serverUser, ConstantPool.ComponentCenter.SERVER_ONLINE);
                }
            }
        }
        //巡检完毕。
        logger.info("=======服务器巡检结束 结束时间:{}", sdf.format(new Date()) + "=======");
    }

    /**
     * @Description: wabSocket发送服务器状态
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/2
     */
    @Async
    @Scheduled(cron = "0/15 * * * * ? ")//0s 开始执行，每15s执行一次
    public void sendServerStatus() {
        if (!ServerResumeWebSocketHandler.sessionMapIsEmpty()) {
            for (String userId : ServerResumeWebSocketHandler.sessionMapKeySet()) {
                List<ServerResumeDto> serverResumes = serverService.getServerResumes(Integer.valueOf(userId));
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    String serverResumesJson = objectMapper.writeValueAsString(serverResumes);
                    ServerResumeWebSocketHandler.senMessage(Integer.valueOf(userId), serverResumesJson);
                } catch (IOException e) {
                    logger.error("定时发送异常");
                    logger.error("异常信息:{}", e.getMessage());
                }
            }
        }
    }

    /**
     * @Description: 维护服务器在线时间 60秒一次
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    @Async
    @Scheduled(cron = "0 * * * * ?")//0秒执行
    public void maintenanceServerTimeKeeping() {
        logger.info("=======维护服务器在线时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "=======");
        Set<String> serverUsers = redisUtil.hkeys(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP);
        for (String serverUser : serverUsers) {
            List<String> serverStatus = redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, serverUser);
            if (!serverStatus.contains(null) && ConstantPool.ComponentCenter.SERVER_ONLINE.equals(serverStatus.get(0))) {
                //如果在线
                //那么维护该服务器在线时间
                redisUtil.incr(ConstantPool.ComponentCenter.SERVER_TIME_KEEPING_KEY + serverUser);
            } else if (!serverStatus.contains(null) && ConstantPool.ComponentCenter.SERVER_OFFLINE.equals(serverStatus.get(0))) {
                //如果不在线，那么直接从缓存中删除这个key
                redisUtil.del(ConstantPool.ComponentCenter.SERVER_TIME_KEEPING_KEY + serverUser);
            }
        }
    }

    /**
     * @Description: 执行计划任务
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/4/23
     */
    @Async
    @Scheduled(cron = "0 * * * * ?")//0秒执行
    public void futureTaskExecutor() {
        Date date = new Date();
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        logger.info("=======执行计划任务:" + nowDate + "=======");

        //1.从数据库中查询是否有这一分钟的计划任务
        List<FutureTask> futureTasks = componentDao.queryFutureTaskForExecute(nowDate);
        //查询之后交给线程池解决，不占用系统主线程
        Runnable executeFutureTasks = new Runnable() {
            @Override
            public void run() {
                //由于session连接是重操作，所以尽量使用缓存，最后一起断开连接
                Map<String, Session> needDisconnectSessions = new HashMap<>();
                for (FutureTask task : futureTasks) {
                    //1.先获取连接会话
                    Integer userId = task.getCreateUserId();
                    String serverIp = task.getExecuteServerIp();
                    ServerSSHInfoDto serverSSHInfoDto = componentDao.queryServerSSHInfoByUserIdAndServerIp(userId, serverIp);
                    JSch jSch = new JSch();
                    Session session = null;
                    Channel channel = null;
                    String result = null;
                    try {
                        if (needDisconnectSessions.containsKey(serverIp)) {
                            session = needDisconnectSessions.get(serverIp);
                        } else {
                            session = jSch.getSession(serverSSHInfoDto.getSshUser(), serverIp);
                            if (session == null) {
                                logger.error("无法连接该服务器");
                                throw new Exception("无法连接该服务器");
                            }
                            //设置密码
                            session.setPassword(serverSSHInfoDto.getSshPassword());
                            //设置第一次登陆的时候提示，可选值：(ask | yes | no)
                            session.setConfig("StrictHostKeyChecking", "no");
                            //超时时间
                            session.connect(30000);

                            //将session先存到set中，便于复用
                            needDisconnectSessions.put(serverIp, session);
                        }
                        //2.获取Channel
                        channel = session.openChannel("exec");
                    } catch (Exception e) {
                        logger.error("获取连接异常，任务执行失败");
                        logger.error("异常信息:{}", e.getMessage());
                        //更新任务执行情况
                        componentDao.updateFutureTaskExecuted(-1, task.getId(), new Date(), "获取连接异常，任务执行失败", null);
                        continue;
                    }
                    ChannelExec channelExec = null;
                    //执行时首先需要判断是否是脚本任务
                    try {
                        channelExec = (ChannelExec) channel;
                        if (task.getIsShellScript()) {
                            //如果是脚本任务，那么执行脚本
                            //执行shell脚本
                            /*如果是shell脚本，必须获取到返回的流，要不然会因为程序执行太快
                             * 导致channel和session已经被关闭了，脚本也没有调用，所以必须要获取到流
                             * 之后，才能证明执行结束了*/
                            //System.out.println("sh " + task.getShellScriptPath());
                            InputStream inputStream = channelExec.getInputStream();
                            channelExec.setCommand("sh " + task.getShellScriptPath());
                            channelExec.connect();
                            result = IOUtils.toString(inputStream, "utf-8");
                        } else {
                            //否则执行命令
                            //执行命令
                            InputStream inputStream = channelExec.getInputStream();
                            channelExec.setCommand(task.getShellCommand());
                            channelExec.setErrStream(System.err);
                            channelExec.connect();
                            result = IOUtils.toString(inputStream, "utf-8");
                        }
                    } catch (JSchException | IOException e) {
                        logger.error("任务执行时异常,任务执行失败");
                        logger.error("异常信息:{}", e.getMessage());
                        //更新任务执行情况
                        componentDao.updateFutureTaskExecuted(-1, task.getId(), new Date(), "任务执行时异常,任务执行失败", null);
                        continue;
                    } finally {
                        if (channelExec != null)
                            channelExec.disconnect();
                        if (channel != null) {
                            channel.disconnect();
                        }
                    }
                    //如果执行到这里了，说明任务执行成功了，更新执行状况为成功
                    Date finishDate = new Date();
                    String finishDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(finishDate);
                    componentDao.updateFutureTaskExecuted(1, task.getId(), finishDate, "任务执行成功,完成时间:" + finishDateStr, result);
                    logger.info("=======计划任务:{}执行成功，完成时间:{}=======", task.getTaskName(), finishDateStr);
                }
                //将有session断开连接
                for (String ipSession : needDisconnectSessions.keySet()) {
                    //断开连接
                    needDisconnectSessions.get(ipSession).disconnect();
                }
            }
        };
        //通过线程池异步执行任务
        threadPool.execute(executeFutureTasks);
    }
}
