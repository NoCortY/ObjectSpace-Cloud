package cn.objectspace.componentcenter.dao;

import cn.objectspace.componentcenter.pojo.entity.CloudServer;
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
}
