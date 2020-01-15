package cn.objectspace.componentcenter.dao;

import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import org.apache.ibatis.annotations.Mapper;

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
}
