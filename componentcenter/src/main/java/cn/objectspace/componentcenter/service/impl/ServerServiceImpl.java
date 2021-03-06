package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.pojo.entity.URPDto;
import cn.objectspace.common.util.PageUtil;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.common.util.TimeUtil;
import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.*;
import cn.objectspace.componentcenter.pojo.dto.daemon.CpuDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.DiskDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.NetDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.ServerInfoDto;
import cn.objectspace.componentcenter.pojo.dto.record.CpuRecordGroupDto;
import cn.objectspace.componentcenter.pojo.dto.record.DiskRecordGroupDto;
import cn.objectspace.componentcenter.pojo.dto.record.MemRecordDto;
import cn.objectspace.componentcenter.pojo.dto.record.NetRecordGroupDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.FutureTask;
import cn.objectspace.componentcenter.service.ServerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerServiceImpl implements ServerService {
    @Autowired
    ComponentDao componentDao;
    @Autowired
    RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

    private Map<String, Integer> highOverloadMap = new ConcurrentHashMap<>();
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


        //设置当前时间
        Date nowDate = new Date();

        //设置记录时间
        serverInfoDto.setRecordTime(nowDate);
        //放入redis缓存，过期时间为60s，如果60秒内没有接收到下一次心跳，那么说明这个服务器宕机。
        redisUtil.set(SerializeUtil.serialize(serverUser+":"+serverIp),SerializeUtil.serialize(serverInfoDto), ConstantPool.ComponentCenter.SERVER_HEARTBEAT_EXTIME);

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

        //高负载数量
        Integer highCount = 0;
        if (serverIpList != null) {
            serverResumeDtos = new ArrayList<>();
            for (String serverIp : serverIpList) {
                ServerResumeDto serverResumeDto = new ServerResumeDto();
                List<String> onlineStatus = redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, userId + ":" + serverIp);

                serverResumeDto.setServerIp(serverIp);
                byte[] serverInfoBytes = redisUtil.get(SerializeUtil.serialize(userId + ":" + serverIp));
                if (ConstantPool.ComponentCenter.SERVER_ONLINE.equals(onlineStatus.get(0)) && serverInfoBytes != null && serverInfoBytes.length > 0) {
                    //在线
                    serverResumeDto.setOnlineStatus(true);
                } else {
                    //如果不在线，那么直接将所有数据封装为0
                    serverResumeDto.setOnlineStatus(false);
                    serverResumeDto.setCpuUsedPercent(0.0);
                    serverResumeDto.setDiskUsedPercent(0.0);
                    serverResumeDto.setMemUsedPercent(0.0);
                    serverResumeDto.setSwapUsedPercent(0.0);
                    serverResumeDto.setSendPackageTotal(0L);
                    serverResumeDto.setRecPackageTotal(0L);
                    serverResumeDtos.add(serverResumeDto);
                    continue;
                }
                ServerInfoDto serverInfoDto = (ServerInfoDto) SerializeUtil.unSerialize(serverInfoBytes);
                Double memUsedPercent = serverInfoDto.getMemUsedPercent();
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
                //高负载发现
                if (memUsedPercent > 80.0 || swapUsedPercent > 80.0 || cpuUsedPercent > 0.8) {
                    highCount++;
                }
                serverResumeDto.setMemUsedPercent(memUsedPercent);
                serverResumeDto.setSwapUsedPercent(swapUsedPercent);
                serverResumeDto.setDiskUsedPercent(diskUsedPercent);
                serverResumeDto.setCpuUsedPercent(cpuUsedPercent);
                serverResumeDto.setSendPackageTotal(sendPackageTotal);
                serverResumeDto.setRecPackageTotal(recPackageTotal);
                serverResumeDtos.add(serverResumeDto);
            }
            highOverloadMap.put(ConstantPool.ComponentCenter.HIGH_OVERLOAD_KEY + userId, highCount);

        } else {
            //服务器列表为空
            logger.info("该用户没有正在监控的服务器");
            return null;
        }
        return serverResumeDtos;
    }

    @Override
    public Integer getServerCount() {
        Integer count = null;

        try {
            count = componentDao.queryServerCount();
        } catch (Exception e) {
            logger.error("获取托管服务器数量异常");
            logger.error("异常信息:{}", e.getMessage());
            return null;
        }
        return count;

    }

    /**
     * @Description: 查询一段时间CPU运行状况
     * @Param: [userId, serverIp, intervalMinutes]
     * @return: cn.objectspace.componentcenter.pojo.dto.record.CpuRecordGroupDto
     * @Author: NoCortY
     * @Date: 2020/3/4
     */
    @Override
    public List<CpuRecordGroupDto> getRuntimeCpuRecord(Integer userId, String serverIp, Long intervalMinutes) {
        if (userId == null || StringUtils.isBlank(serverIp) || "undefined".equals(serverIp)) {
            logger.info("用户名和服务器IP为必填项");
            return null;
        }
        String lastRecordTime = componentDao.queryLastRecordTimeByUserIdAndServerIp(userId, serverIp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastHeartBeat = null;
        try {
            lastHeartBeat = sdf.parse(lastRecordTime);
        } catch (ParseException e) {
            logger.error("日期转换异常");
            logger.error("异常信息:{}", e.getMessage());
            lastHeartBeat = new Date();
        }
        Date endTime = lastHeartBeat;
        //时间戳
        Date startTime = new Date(endTime.getTime() - TimeUtil.minuteToMillisecond(intervalMinutes));

        List<CpuRecordGroupDto> cpuRecordGroupDtos = null;
        try {
            cpuRecordGroupDtos = componentDao.queryCpuRuntimeRecord(userId, serverIp, startTime, endTime);
        } catch (Exception e) {
            logger.error("查询cpu间隔时间运行情况异常");
            logger.error("异常信息:{}", e.getMessage());
        }
        return cpuRecordGroupDtos;
    }

    @Override
    public List<DiskRecordGroupDto> getRuntimeDiskRecord(Integer userId, String serverIp, Long intervalMinutes) {
        if (userId == null || StringUtils.isBlank(serverIp) || "undefined".equals(serverIp)) {
            logger.info("用户名和服务器IP为必填项");
            return null;
        }
        String lastRecordTime = componentDao.queryLastRecordTimeByUserIdAndServerIp(userId, serverIp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastHeartBeat = null;
        try {
            lastHeartBeat = sdf.parse(lastRecordTime);
        } catch (ParseException e) {
            logger.error("日期转换异常");
            logger.error("异常信息:{}", e.getMessage());
            lastHeartBeat = new Date();
        }
        Date endTime = lastHeartBeat;
        //时间戳
        Date startTime = new Date(endTime.getTime() - TimeUtil.minuteToMillisecond(intervalMinutes));

        List<DiskRecordGroupDto> diskRecordGroupDtos = null;
        try {
            diskRecordGroupDtos = componentDao.queryDiskRuntimeRecord(userId, serverIp, startTime, endTime);
        } catch (Exception e) {
            logger.error("查询硬盘间隔时间运行情况异常");
            logger.error("异常信息:{}", e.getMessage());
        }
        return diskRecordGroupDtos;
    }

    @Override
    public List<MemRecordDto> getRuntimeMemRecord(Integer userId, String serverIp, Long intervalMinutes) {
        if (userId == null || StringUtils.isBlank(serverIp) || "undefined".equals(serverIp)) {
            logger.info("用户名和服务器IP为必填项");
            return null;
        }
        String lastRecordTime = componentDao.queryLastRecordTimeByUserIdAndServerIp(userId, serverIp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastHeartBeat = null;
        try {
            lastHeartBeat = sdf.parse(lastRecordTime);
        } catch (ParseException e) {
            logger.error("日期转换异常");
            logger.error("异常信息:{}", e.getMessage());
            lastHeartBeat = new Date();
        }
        Date endTime = lastHeartBeat;
        //时间戳
        Date startTime = new Date(endTime.getTime() - TimeUtil.minuteToMillisecond(intervalMinutes));

        List<MemRecordDto> memRecordDtos = null;
        try {
            memRecordDtos = componentDao.queryMemRuntimeRecord(userId, serverIp, startTime, endTime);
        } catch (Exception e) {
            logger.error("查询硬盘间隔时间运行情况异常");
            logger.error("异常信息:{}", e.getMessage());
        }
        return memRecordDtos;
    }

    /**
     * @Description: 查询网卡运行时记录
     * @Param: [userId, serverIp, intervalMinutes]
     * @return: java.util.List<cn.objectspace.componentcenter.pojo.dto.record.NetRecordGroupDto>
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    @Override
    public List<NetRecordGroupDto> getRuntimeNetRecord(Integer userId, String serverIp, Long intervalMinutes) {
        if (userId == null || StringUtils.isBlank(serverIp) || "undefined".equals(serverIp)) {
            logger.info("用户名和服务器IP为必填项");
            return null;
        }
        String lastRecordTime = componentDao.queryLastRecordTimeByUserIdAndServerIp(userId, serverIp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastHeartBeat = null;
        try {
            lastHeartBeat = sdf.parse(lastRecordTime);
        } catch (ParseException e) {
            logger.error("日期转换异常");
            logger.error("异常信息:{}", e.getMessage());
            lastHeartBeat = new Date();
        }
        Date endTime = lastHeartBeat;
        //时间戳
        Date startTime = new Date(endTime.getTime() - TimeUtil.minuteToMillisecond(intervalMinutes));

        List<NetRecordGroupDto> netRecordGroupDtos = null;
        try {
            netRecordGroupDtos = componentDao.queryNetRuntimeRecord(userId, serverIp, startTime, endTime);
        } catch (Exception e) {
            logger.error("查询硬盘间隔时间运行情况异常");
            logger.error("异常信息:{}", e.getMessage());
        }
        return netRecordGroupDtos;
    }

    /**
     * @Description: 获取SSH连接列表
     * @Param: [userId]
     * @return: java.util.List<cn.objectspace.componentcenter.pojo.dto.ServerSSHDto>
     * @Author: NoCortY
     * @Date: 2020/3/9
     */
    @Override
    public ResponseMap<List<ServerSSHDto>> getServerSSHList(Integer userId, Integer page, Integer limit) {
        if (userId == null) {
            logger.info("用户id为必填项");
            return null;
        }
        Integer startItem = null;
        //为什么要有这个判断？
        //因为我不想写一个新的方法来给不需要分页的地方使用了
        //也不想更改controller的参数
        //所以就让前端直接传-1，代表不需要分页
        if (page != -1 && limit != -1) {
            startItem = PageUtil.getRowIndex(page, limit);
        } else {
            limit = null;
        }
        ResponseMap<List<ServerSSHDto>> responseMap = new ResponseMap<>();
        List<ServerSSHDto> serverSSHDtos = null;
        try {
            serverSSHDtos = componentDao.queryLinuxServerSSHByUserId(userId, startItem, limit);
            if (serverSSHDtos != null) {
                for (ServerSSHDto serverSSH : serverSSHDtos) {
                    //这里有可能get出来的是空，由于Redis有可能宕机
                    //但是暂做标记，测试后处理
                    List<String> status = redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, userId + ":" + serverSSH.getServerIp());
                    if (ConstantPool.ComponentCenter.SERVER_ONLINE.equals(status.get(0))) {
                        serverSSH.setServerStatus("在线");
                    } else {
                        serverSSH.setServerStatus("离线");
                    }
                }
            }
            responseMap.setData(serverSSHDtos);
            responseMap.setCount(componentDao.queryCountLinuxServerSSHByUserId(userId));
        } catch (Exception e) {
            logger.error("获取服务器SSH列表异常");
            logger.error("异常信息:" + e.getMessage());
            return null;
        }

        return responseMap;
    }

    /**
     * @Description: 更新SSH连接信息
     * @Param: [cloudServer]
     * @return: java.lang.Integer
     * @Author: NoCortY
     * @Date: 2020/3/10
     */
    @Override
    public Integer renewServerSSHInfo(CloudServer cloudServer) {
        if (cloudServer == null || StringUtils.isBlank(cloudServer.getServerIp()) || cloudServer.getServerUser() == null || StringUtils.isBlank(cloudServer.getSshUser()) || StringUtils.isBlank(cloudServer.getSshPassword())) {
            logger.info("缺少必填项");
            return null;
        }
        if (StringUtils.isBlank(cloudServer.getSshPort())) {
            logger.info("设置默认端口号为22");
            cloudServer.setSshPort("22");
        }
        Integer effectiveNum = null;
        try {
            effectiveNum = componentDao.updateSSHInfo(cloudServer);
        } catch (Exception e) {
            logger.error("更新SSH信息异常");
            logger.error("异常信息:{}", e.getMessage());
            return null;
        }
        return effectiveNum;
    }

    @Override
    public ServerSimpleStatusDto getServerSimpleStatus(Integer userId) {
        List<String> serverIpList = componentDao.queryServerIpByUserId(userId);
        ServerSimpleStatusDto serverSimpleStatusDto = new ServerSimpleStatusDto();
        Integer highCount = highOverloadMap.get(ConstantPool.ComponentCenter.HIGH_OVERLOAD_KEY + userId);
        if (highCount == null) highCount = 0;
        serverSimpleStatusDto.setHighOverload(0);
        int down = 0, normal = 0;
        for (String serverIp : serverIpList) {
            String key = userId + ":" + serverIp;
            if (ConstantPool.ComponentCenter.SERVER_ONLINE.equals(redisUtil.hmget(ConstantPool.ComponentCenter.MONITOR_SERVER_MAP, key).get(0))) {
                normal++;
            } else {
                down++;
            }
        }
        //正常运行的数量需要扣除掉高负载数量
        normal = normal - highCount;
        serverSimpleStatusDto.setNormalCount(normal);
        serverSimpleStatusDto.setHighOverload(highCount);
        serverSimpleStatusDto.setDown(down);
        return serverSimpleStatusDto;
    }

    @Override
    public ServerSimpleSnapshot getServerSimpleSnapshot(Integer userId, String serverIp) {
        if (userId == null || StringUtils.isBlank(serverIp)) {
            logger.info("not null");
            return null;
        }
        ServerSimpleSnapshot serverSimpleSnapshot = new ServerSimpleSnapshot();
        //获取详细信息
        ServerDetailDto serverDetailDto = componentDao.queryDetailOfServerByUserIdAndServerIp(serverIp, userId);
        //计算硬盘大小
        Double diskTotal = 0.0;
        for (DiskDetailDto diskDetailDto : serverDetailDto.getDiskDetailDtos()) {
            diskTotal += Double.parseDouble(diskDetailDto.getTotal()) / 1024;
        }
        //计算内存大小
        Double memTotal = Double.valueOf(serverDetailDto.getMemTotal());
        serverSimpleSnapshot.setDisk(diskTotal / 1024);
        serverSimpleSnapshot.setMemory(memTotal / 1024 / 1024);


        //拿到最后心跳时间
        ServerInfoDto serverInfoDto = (ServerInfoDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(userId + ":" + serverIp)));
        Date lastHeartBeat = new Date();
        if (serverInfoDto == null) {
            //如果为null,说明已经down了
            //那么需要去数据库中拿最后的心跳时间
            String lastRecordTime = componentDao.queryLastRecordTimeByUserIdAndServerIp(userId, serverIp);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                lastHeartBeat = simpleDateFormat.parse(lastRecordTime);
            } catch (ParseException e) {
                logger.error("日期转换失败");
                logger.error("异常信息:{}", e.getMessage());
            }
            serverSimpleSnapshot.setLastHeartBeat(lastHeartBeat);
            return serverSimpleSnapshot;
        } else {
            lastHeartBeat = serverInfoDto.getRecordTime();
            serverSimpleSnapshot.setLastHeartBeat(lastHeartBeat);
        }
        String timeKeeping = redisUtil.get(ConstantPool.ComponentCenter.SERVER_TIME_KEEPING_KEY + userId + ":" + serverIp);
        serverSimpleSnapshot.setTimeKeeping(Integer.valueOf(timeKeeping));
        return serverSimpleSnapshot;
    }

    /**
     * @Description: 获取服务器大事记录
     * @Param: [userId]
     * @return: java.util.List<cn.objectspace.componentcenter.pojo.dto.ServerRuntimeRecordDto>
     * @Author: NoCortY
     * @Date: 2020/4/22
     */
    @Override
    public List<ServerRuntimeRecordDto> getServerRuntimeRecord(Integer userId) {

        if (userId == null) {
            logger.info("userId不能为空");
            return null;
        }
        List<ServerRuntimeRecordDto> serverRuntimeRecordList = null;
        try {
            serverRuntimeRecordList = componentDao.queryServerRuntimeRecords(userId);
        } catch (Exception e) {
            logger.error("查询出现异常");
            logger.error("异常信息:{}", e.getMessage());
        }
        return serverRuntimeRecordList;
    }

    /**
     * @Description: 新增计划任务
     * @Param: [futureTask, userId]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/4/23
     */
    @Override
    public boolean addFutureTask(FutureTask futureTask, Integer userId) {
        if (futureTask == null || userId == null) {
            logger.info("用户id或者计划任务对象不能为空");
            return false;
        }
        if (futureTask.getExecuteTime().getTime() < new Date().getTime()) {
            logger.info("不能计划过去时间的任务");
            return false;
        }
        //获取用户信息
        URPDto urpDto = (URPDto) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC + userId)));
        if (urpDto == null) {
            logger.info("权限不足");
            return false;
        }
        futureTask.setCreateUserId(userId);
        futureTask.setCreateUserName(urpDto.getUserName());
        //设置为未执行
        futureTask.setExecuted(0);
        int effectiveNum = componentDao.insertFutureTask(futureTask);

        return effectiveNum > 0;
    }

    @Override
    public List<FutureTaskDto> getFutureTaskList(Integer userId, Integer page, Integer limit) {
        if (userId == null) {
            logger.info("userId不能为空");
            return null;
        }
        Integer startItem = PageUtil.getRowIndex(page, limit);
        return componentDao.queryFutureTaskList(userId, startItem, limit);
    }

    @Override
    public Integer getFutureTaskListCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return componentDao.queryFutureTaskCount(userId);
    }

}
