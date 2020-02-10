package cn.objectspace.componentcenter.dao;

import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.daemon.CpuDto;
import cn.objectspace.componentcenter.pojo.entity.daemon.DiskDto;
import cn.objectspace.componentcenter.pojo.entity.daemon.NetDto;
import cn.objectspace.componentcenter.pojo.entity.daemon.ServerInfoDto;
import org.apache.ibatis.annotations.Mapper;

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
    public List<CloudServer> queryServerByUserId(Integer userId);

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

    public Integer insertNetStateInfo(List<NetDto> netDtoList);

}
