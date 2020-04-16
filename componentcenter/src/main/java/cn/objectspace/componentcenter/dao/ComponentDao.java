package cn.objectspace.componentcenter.dao;

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
import cn.objectspace.componentcenter.pojo.entity.CloudServerCommandExecuteRecord;
import cn.objectspace.componentcenter.pojo.entity.SimpleCommand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: 组件注册
 * @Author: NoCortY
 * @Date: 2020/1/15
 */
@Mapper
public interface ComponentDao {
    /**
     * @Description: 注册数据库
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/1/15
     */
    public Integer insertCloudServer(CloudServer cloudServer);

    /**
     * @Description: 用户 查询自己名下的所有服务器
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/6
     */
    public List<CloudServerDto> queryServerByUserId(@Param("userId") Integer userId, @Param("startItem") Integer startItem, @Param("limit") Integer limit);

    /**
     * @Description: 自己名下的服务器个数
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    public Integer queryCountOfServerByUserId(@Param("userId") Integer userId);

    /**
     * @Description: 根据用户ID和服务器IP来查询服务器
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/12
     */
    public CloudServerDto queryServerByUserIdAndServerIp(@Param("userId") Integer userId, @Param("serverIp") String serverIp);

    /**
     * @Description: 记录服务器状态
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/10
     */
    public Integer insertServerStateInfo(ServerInfoDto serverInfoDto);

    /**
     * @Description: 记录每块CPU核心状态
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/10
     */
    public Integer insertCpuStateInfo(List<CpuDto> cpuDtoList);

    /**
     * @Description: 记录每块硬盘状态
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/10
     */
    public Integer insertDiskStateInfo(List<DiskDto> diskDtoList);


    /**
     * @Description: 记录网卡状态
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/12
     */
    public Integer insertNetStateInfo(List<NetDto> netDtoList);

    /**
     * @Description: 通过用户ID和服务器IP查询服务器详细信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    public ServerDetailDto queryDetailOfServerByUserIdAndServerIp(@Param("serverIp") String serverIp, @Param("userId") Integer userId);

    /**
     * @Description: 通过用户id查询所有服务器的IP
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/3
     */
    public List<String> queryServerIpByUserId(@Param("userId") Integer userId);


    /**
     * @Description: 查询已经注册的服务器列表。
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/3
     */
    public List<CloudServer> queryRegisteredServer();


    /**
     * @Description: 查询服务器数量
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/3
     */
    public Integer queryServerCount();


    /**
     * @Description: 按照时间段，查询CPU负载记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/4
     */
    public List<CpuRecordGroupDto> queryCpuRuntimeRecord(@Param("userId") Integer userId, @Param("serverIp") String serverIp,
                                                         @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    /**
     * @Description: 按照时间段，查询硬盘读写记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    public List<DiskRecordGroupDto> queryDiskRuntimeRecord(@Param("userId") Integer userId, @Param("serverIp") String serverIp,
                                                           @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * @Description: 按照时间段 查询内存使用记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/6
     */
    public List<MemRecordDto> queryMemRuntimeRecord(@Param("userId") Integer userId, @Param("serverIp") String serverIp,
                                                    @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    /**
     * @Description: 按照时间段查询网卡运行时记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    public List<NetRecordGroupDto> queryNetRuntimeRecord(@Param("userId") Integer userId, @Param("serverIp") String serverIp,
                                                         @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * @Description: 查询linux终端SSH连接信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/9
     */
    public List<ServerSSHDto> queryLinuxServerSSHByUserId(@Param("userId") Integer userId, @Param("startItem") Integer startItem, @Param("limit") Integer limit);

    /**
     * @Description: 查询linux终端SSH连接信息的数量
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/9
     */
    public Integer queryCountLinuxServerSSHByUserId(@Param("userId") Integer userId);

    /**
     * @Description: 更新服务器SSH有关信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/10
     */
    public Integer updateSSHInfo(CloudServer cloudServer);

    /**
     * @Description: 查询服务器连接所需的SSH信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/10
     */
    public ServerSSHInfoDto queryServerSSHInfoByUserIdAndServerIp(@Param("userId") Integer userId, @Param("serverIp") String serverIp);


    /**
     * @Description: 记录服务器执行命令
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public Integer insertExecuteCommandRecord(List<CloudServerCommandExecuteRecord> cloudServerCommandExecuteRecords);

    /**
     * @Description: 通过用户id查找服务器执行命令记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public List<CloudServerCommandExecuteRecordDto> queryServerCommandExecuteRecordByUserId(@Param("userId") Integer userId);

    /**
     * @Description: 查询常用命令
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public List<SimpleCommandDto> querySimpleCommandList(@Param("userId") Integer userId);

    /**
     * @Description: 插入常用命令
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public Integer insertSimpleCommand(SimpleCommand simpleCommand);

    /**
     * @Description: 根据id删除常用命令
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public Integer deleteSimpleCommandById(@Param("id") Integer id, @Param("userId") Integer userId);
}
