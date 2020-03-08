package cn.objectspace.componentcenter.scheduled;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.componentcenter.controller.websocket.ServerResumeWebSocketHandler;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.ServerResumeDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
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

    private static Logger logger = LoggerFactory.getLogger(ServerPatrol.class);

    /**
     * @Description: 巡检服务器在线状态
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
                if (bytes == null || bytes.length <= 0) {
                    //说明已经离线了
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, key, ConstantPool.ComponentCenter.SERVER_OFFLINE);
                } else {
                    //说明在线
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, key, ConstantPool.ComponentCenter.SERVER_ONLINE);
                }
            }
        } else {
            //如果不为空，说明redis没有宕机，即使数据与数据库中不相同，发送心跳时也会同步
            for (String serverUser : serverUsers) {
                byte[] bytes = redisUtil.get(SerializeUtil.serialize(serverUser));
                if (bytes == null || bytes.length <= 0) {
                    //离线
                    redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, serverUser, ConstantPool.ComponentCenter.SERVER_OFFLINE);
                } else {
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
                    String serverResumesJson = serverResumesJson = objectMapper.writeValueAsString(serverResumes);
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
