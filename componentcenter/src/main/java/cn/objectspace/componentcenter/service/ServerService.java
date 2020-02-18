package cn.objectspace.componentcenter.service;

import cn.objectspace.componentcenter.pojo.dto.CloudServerDto;
import cn.objectspace.componentcenter.pojo.dto.ServerDetailDto;
import cn.objectspace.componentcenter.pojo.dto.ServerResumeDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.ServerInfoDto;
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

    public List<ServerResumeDto> getServerResumes(Integer userId);
}
