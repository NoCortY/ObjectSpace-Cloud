package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.util.PageUtil;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.CloudServerDto;
import cn.objectspace.componentcenter.pojo.dto.ServerDetailDto;
import cn.objectspace.componentcenter.pojo.dto.ServerResumeDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.CpuDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.DiskDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.NetDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.ServerInfoDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.service.ServerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Date;
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
    public List<CloudServerDto> getMySelfServer(Integer userId, Integer page, Integer limit) {
        if(userId==null){
            logger.info("用户ID为空是非法操作");
            return null;
        }
        return componentDao.queryServerByUserId(userId, PageUtil.getRowIndex(page,limit),limit);
    }

    /**
     * @Description: 获取自己名下服务器台数
     * @Param: [userId]
     * @return: java.lang.Integer
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    @Override
    public Integer getCountOfMySelfServer(Integer userId) {
        if(userId==null){
            logger.info("用户ID为空是非法操作");
            return null;
        }
        return componentDao.queryCountOfServerByUserId(userId);
    }

    /**
     * @Description: 获取服务器详细配置信息
     * @Param: [serverIp, userId]
     * @return: cn.objectspace.componentcenter.pojo.dto.ServerDetailDto
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    @Override
    public ServerDetailDto getServerDetail(String serverIp, Integer userId) {
        if(StringUtils.isBlank(serverIp)||userId==null){
            logger.error("查询详细信息时服务器IP和userId缺失为非法操作");
            return null;
        }
        return componentDao.queryDetailOfServerByUserIdAndServerIp(serverIp,userId);
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
        // System.out.println("key1:"+serverUser+":"+serverIp);
        //hmget返回的是List<String> 不能使用==null判空
        if(redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP,serverUser+":"+serverIp).contains(null)){
            //如果redis服务器监控列表中 没有get到该服务器，那么说明这个服务器是第一次注册，那么一定要往服务器注册表中插入该服务器信息
            //注册进入数据库
            if(componentDao.queryServerByUserIdAndServerIp(serverUser,serverIp)==null){
                //如果数据库中没有才重新写入，否则不写入，避免因为redis数据丢失造成数据重复写入
                CloudServer cloudServer = new CloudServer();
                cloudServer.setServerIp(serverIp);
                cloudServer.setServerName(serverInfoDto.getComputerName());
                cloudServer.setServerOsType(serverInfoDto.getOsName());
                cloudServer.setServerOsVersion(serverInfoDto.getOsVersion());
                cloudServer.setIsMonitor(true);
                cloudServer.setServerUser(serverUser);
                try{
                    componentDao.insertCloudServer(cloudServer);
                }catch (Exception e){
                    logger.error("服务器注册失败!");
                    logger.error("异常信息:{}",e.getMessage());
                    return false;
                }
            }
            //设置redis中该服务器状态为在线
            redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP,serverUser+":"+serverIp,ConstantPool.ComponentCenter.SERVER_ONLINE);

        }
        //System.out.println("key2:"+serverUser+":"+serverIp);
        if(ConstantPool.ComponentCenter.SERVER_OFFLINE.equals(redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP,serverUser+":"+serverIp))){
            //如果redis中get到这个服务器为离线状态，可能之前是宕机
            //不论怎样，都需要维护在线列表
            redisUtil.hmset(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP,serverUser+":"+serverIp,ConstantPool.ComponentCenter.SERVER_ONLINE);
        }

        //放入redis缓存，过期时间为60s，如果60秒内没有接收到下一次心跳，那么说明这个服务器宕机。
        redisUtil.set(SerializeUtil.serialize(serverUser+":"+serverIp),SerializeUtil.serialize(serverInfoDto), ConstantPool.ComponentCenter.SERVER_HEARTBEAT_EXTIME);
        //设置当前时间
        Date nowDate = new Date();
        ArrayList<CpuDto> cpuList = (ArrayList<CpuDto>) serverInfoDto.getCpuList();
        ArrayList<DiskDto> diskList = (ArrayList<DiskDto>) serverInfoDto.getDiskList();
        ArrayList<NetDto> netList = (ArrayList<NetDto>) serverInfoDto.getNetList();

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

        return true;
    }

    @Override
    public List<ServerResumeDto> getServerResumes(Integer userId) {
        if (userId == null) {
            logger.error("userId不能为空");
            return null;
        }
        List<ServerResumeDto> serverResumeDtos = null;
        List<String> serverIpList = componentDao.queryServerIpByUserId(userId);
        if (serverIpList != null) {
            serverResumeDtos = new ArrayList<>();
            for (String serverIp : serverIpList) {
                ServerResumeDto serverResumeDto = new ServerResumeDto();
                List<String> onlineStatus = redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, userId + ":" + serverIp);
                if (ConstantPool.ComponentCenter.SERVER_ONLINE.equals(onlineStatus.get(0))) {
                    //在线
                    serverResumeDto.setOnlineStatus(true);
                } else {
                    //如果不在线，那么直接将所有数据封装为0
                    serverResumeDto.setOnlineStatus(false);
                    serverResumeDto.setCpuUsedPercent(0.0);
                    serverResumeDto.setDiskUsedPercent(0.0);
                    serverResumeDto.setMemUsedPercent(0.0);
                    serverResumeDto.setRecPackageTotal(0L);
                    serverResumeDto.setRecPackageTotal(0L);
                    serverResumeDtos.add(serverResumeDto);
                    continue;
                }
                ServerInfoDto serverInfoDto = (ServerInfoDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(userId + ":" + serverIp)));
                Double memUsedPercent = serverInfoDto.getMemUsed().doubleValue() / serverInfoDto.getMemTotal().doubleValue();
                Double swapUsedPercent = serverInfoDto.getSwapUsedPercent();
                double cpuUsedTotal = 0;
                //CPU
                for (CpuDto cpuDto : serverInfoDto.getCpuList()) {
                    cpuUsedTotal += cpuDto.getSystemUsed() + cpuDto.getUserUsed();
                }
                Double cpuUsedPercent = cpuUsedTotal / serverInfoDto.getCpuList().size();
                double diskUsedTotal = 0;
                //DISK
                for (DiskDto diskDto : serverInfoDto.getDiskList()) {
                    diskUsedTotal += diskDto.getUsePercent();
                }
                Double diskUsedPercent = diskUsedTotal / serverInfoDto.getDiskList().size();
                Long sendPackageTotal = 0L;
                Long recPackageTotal = 0L;
                //NET
                for (NetDto netDto : serverInfoDto.getNetList()) {
                    sendPackageTotal += netDto.getTxPackets();
                    recPackageTotal += netDto.getRxPackets();
                }
                serverResumeDto.setMemUsedPercent(memUsedPercent);
                serverResumeDto.setSwapUsedPercent(swapUsedPercent);
                serverResumeDto.setDiskUsedPercent(diskUsedPercent);
                serverResumeDto.setCpuUsedPercent(cpuUsedPercent);
                serverResumeDto.setSendPackageTotal(sendPackageTotal);
                serverResumeDto.setRecPackageTotal(recPackageTotal);
                serverResumeDtos.add(serverResumeDto);
            }
        } else {
            //服务器列表为空
            logger.info("该用户没有正在监控的服务器");
            return null;
        }
        return serverResumeDtos;
    }

}
