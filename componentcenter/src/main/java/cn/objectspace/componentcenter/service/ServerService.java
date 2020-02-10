package cn.objectspace.componentcenter.service;

import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.daemon.ServerInfoDto;

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
    public List<CloudServer> getMySelfServer(Integer userId);

    /**
     * @Description: 心跳业务逻辑
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/2/10
     */
    public boolean ping(ServerInfoDto serverInfoDto);
}
