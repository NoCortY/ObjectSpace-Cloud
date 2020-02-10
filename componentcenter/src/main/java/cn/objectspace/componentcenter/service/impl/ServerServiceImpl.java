package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.daemon.CpuDto;
import cn.objectspace.componentcenter.pojo.entity.daemon.DiskDto;
import cn.objectspace.componentcenter.pojo.entity.daemon.NetDto;
import cn.objectspace.componentcenter.pojo.entity.daemon.ServerInfoDto;
import cn.objectspace.componentcenter.service.ServerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class ServerServiceImpl implements ServerService {
    @Autowired
    ComponentDao componentDao;
    @Autowired
    RedisUtil redisUtil;
    Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);
    @Override
    public boolean registerServer(CloudServer cloudServer) {
        if(cloudServer==null||cloudServer.getServerIp()==null||cloudServer.getServerIp().length()==0) return false;
        int effectiveNum = 0;
        try{
            effectiveNum = componentDao.insertCloudServer(cloudServer);
        }catch (Exception e){
            System.out.println(effectiveNum);
            logger.error("注册服务器出现异常");
            logger.error("异常信息:{}",e.getMessage());
            e.printStackTrace();
            return false;
        }
        if(effectiveNum<=0) return false;
        else return true;
    }

    @Override
    public List<CloudServer> getMySelfServer(Integer userId) {
        if(userId==null){
            logger.info("用户ID为空是非法操作");
            return null;
        }
        return componentDao.queryServerByUserId(userId);
    }

    /**
     * @Description: 心跳业务逻辑   当守护线程传送心跳到组件管理器时，组件管理器需要将服务器的状态保存下来，
     * 直接插入数据库和redis，redis只需要保存当前服务器状态即可，而数据库中需要保存历史状态。
     * @Param: [serverInfoDto]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/2/10
     */
    @Override
    @Transactional
    public boolean ping(ServerInfoDto serverInfoDto) {
        if(StringUtils.isBlank(serverInfoDto.getIp())||serverInfoDto.getServerUser()==null){
            logger.error("服务器IP或服务器归属用户不能为空");
            return false;
        }
        String serverIp = serverInfoDto.getIp();
        Integer serverUser = serverInfoDto.getServerUser();
        if(redisUtil.get(SerializeUtil.serialize(serverUser+":"+serverIp))==null){
            //如果redis中没有get到这个服务器，那么说明这台服务器有可能是第一次发送心跳，也有可能是宕机
            //不论怎样，都需要维护在线列表
            redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP,serverUser+":"+serverIp,ConstantPool.ComponentCenter.SERVER_ONLINE);
        }

        //放入redis缓存，过期时间为60s，如果60秒内没有接收到下一次心跳，那么说明这个服务器宕机。
        redisUtil.set(SerializeUtil.serialize(serverUser+":"+serverIp),SerializeUtil.serialize(serverInfoDto), ConstantPool.ComponentCenter.SERVER_HEARTBEAT_EXTIME);
        //设置当前时间
        Date nowDate = new Date();
        LinkedList<CpuDto> cpuList = (LinkedList<CpuDto>) serverInfoDto.getCpuList();
        LinkedList<DiskDto> diskList = (LinkedList<DiskDto>) serverInfoDto.getDiskList();
        LinkedList<NetDto> netList = (LinkedList<NetDto>) serverInfoDto.getNetList();

        for(CpuDto cpuDto:cpuList){
            //设置CPU识别信息
            cpuDto.setCpuServerIp(serverIp);
            cpuDto.setCpuServerUser(serverUser);
            cpuDto.setRecordTime(nowDate);
        }
        for(DiskDto diskDto:diskList){
            //设置硬盘识别信息
            diskDto.setDiskServerIp(serverIp);
            diskDto.setDiskServerUser(serverUser);
            diskDto.setRecordTime(nowDate);
        }
        for(NetDto netDto:netList){
            //设置网卡识别信息
            netDto.setNetServerIp(serverIp);
            netDto.setNetServerUser(serverUser);
            netDto.setRecordTime(nowDate);
        }
        //设置记录时间
        serverInfoDto.setRecordTime(nowDate);
        //记录
        try{
            componentDao.insertServerStateInfo(serverInfoDto);
            componentDao.insertCpuStateInfo(cpuList);
            componentDao.insertDiskStateInfo(diskList);
            componentDao.insertNetStateInfo(netList);
        }catch (Exception e){
            logger.error("记录服务器状态异常");
            logger.error("异常信息:{}",e.getMessage());
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }
}
