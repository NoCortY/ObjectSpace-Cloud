package cn.objectspace.componentcenter.service;

import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.componentcenter.pojo.dto.*;
import cn.objectspace.componentcenter.pojo.dto.daemon.ServerInfoDto;
import cn.objectspace.componentcenter.pojo.dto.record.CpuRecordGroupDto;
import cn.objectspace.componentcenter.pojo.dto.record.DiskRecordGroupDto;
import cn.objectspace.componentcenter.pojo.dto.record.MemRecordDto;
import cn.objectspace.componentcenter.pojo.dto.record.NetRecordGroupDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;

import java.util.List;

/**
* @Description: 服务器业务逻辑接口
* @Author: NoCortY
* @Date: 2020/1/15
*/
public interface ServerService {
    /**
     * @Description: 注册服务器
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/1/15
     */
    public boolean registerServer(CloudServer cloudServer);

    /**
     * @Description: 获取自己名下的服务器
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/7
     */
    public List<CloudServerDto> getMySelfServer(Integer userId, Integer page, Integer limit);

    /**
     * @Description: 获取自己名下的服务器的台数
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    public Integer getCountOfMySelfServer(Integer userId);

    /**
     * @Description: 获取服务器详细信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    public ServerDetailDto getServerDetail(String serverIp,Integer userId);

    /**
     * @Description: 心跳业务逻辑
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/10
     */
    public boolean ping(ServerInfoDto serverInfoDto);

    /**
     * @Description: 获取某个用户名下服务器概要信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/3
     */
    public List<ServerResumeDto> getServerResumes(Integer userId);

    /**
     * @Description: 获取系统托管的所有服务器数量
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/3
     */
    public Integer getServerCount();

    /**
     * @Description: 查询一段时间内CPU的运行状况
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/4
     */
    public List<CpuRecordGroupDto> getRuntimeCpuRecord(Integer userId, String serverIp, Long intervalMinutes);

    /**
     * @Description: 查询一段时间内硬盘读写状况
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    public List<DiskRecordGroupDto> getRuntimeDiskRecord(Integer userId, String serverIp, Long intervalMinutes);

    /**
     * @Description: 查询一段时间内存使用情况
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/6
     */
    public List<MemRecordDto> getRuntimeMemRecord(Integer userId, String serverIp, Long intervalMinutes);

    /**
     * @Description: 查询一段时间内的网卡使用情况
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    public List<NetRecordGroupDto> getRuntimeNetRecord(Integer userId, String serverIp, Long intervalMinutes);

    /**
     * @Description: 获取SSH连接列表
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/10
     */
    public ResponseMap<List<ServerSSHDto>> getServerSSHList(Integer userId, Integer page, Integer limit);

    /**
     * @Description: 更新服务器SSH连接信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/10
     */
    public Integer renewServerSSHInfo(CloudServer cloudServer);

    /**
     * @Description: 获取服务器基本状态
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/21
     */
    public ServerSimpleStatusDto getServerSimpleStatus(Integer userId);

    /**
     * @Description: 获取服务器快照简单信息
     * @Author: NoCortY
     * @Date: 2020/4/21
     */
    public ServerSimpleSnapshot getServerSimpleSnapshot(Integer userId, String serverIp);

    /**
     * @Description: 获取服务器大事记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/22
     */
    public List<ServerRuntimeRecordDto> getServerRuntimeRecord(Integer userId);
}
