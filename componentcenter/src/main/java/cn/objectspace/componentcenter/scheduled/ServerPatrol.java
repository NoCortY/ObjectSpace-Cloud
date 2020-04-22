package cn.objectspace.componentcenter.scheduled;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.URPDto;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.componentcenter.controller.websocket.ServerResumeWebSocketHandler;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.ServerResumeDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.ServerRuntimeRecord;
import cn.objectspace.componentcenter.service.MailService;
import cn.objectspace.componentcenter.service.ServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

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


    /**
     * @Description: 巡检服务器在线状态，如果出现异常状态会发送邮件提醒服务器owner
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/2
     */
    @Async
    @Scheduled(fixedRate = 30000)//间隔30s
    public void updateServerStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("服务器巡检开始 开始时间:{}", sdf.format(new Date()));
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
                            Thread emailThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mailService.sendSimpleMail(urpDto.getUserEmail(), "ObjectSpace自动化运维平台-服务器离线通知", "您的服务器 IP:" + s.getServerIp() + " 已停机，请及时处理。");
                                    logger.info("邮件发送至:{},结束", urpDto.getUserEmail());
                                }
                            });
                            emailThread.start();

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
                            Thread thread = new Thread(new Runnable() {
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
                            thread.start();
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
                            Thread thread = new Thread(new Runnable() {
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
                            thread.start();
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
                            Thread thread = new Thread(new Runnable() {
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
                            thread.start();
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
        logger.info("服务器巡检结束 结束时间:{}", sdf.format(new Date()));
    }

    /**
     * @Description: wabSocket发送服务器状态
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/2
     */
    @Async
    @Scheduled(fixedRate = 30000)
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
    @Scheduled(fixedRate = 60000)
    public void maintenanceServerTimeKeeping() {
        logger.info("维护服务器在线时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
}
