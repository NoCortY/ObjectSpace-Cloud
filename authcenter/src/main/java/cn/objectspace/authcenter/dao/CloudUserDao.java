package cn.objectspace.authcenter.dao;

import cn.objectspace.authcenter.pojo.entity.CloudUser;
import org.apache.ibatis.annotations.Mapper;

/**
* @Description: 用户DAO
* @Author: NoCortY
* @Date: 2019/12/16
*/
@Mapper
public interface CloudUserDao {
    /**
     * @Description: 查询用户详细信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public CloudUser queryCloudUser(CloudUser user);
   /**
    * @Description: 查询用户基本信息
    * @Param:
    * @return:
    * @Author: NoCortY
    * @Date: 2019/12/30
    */
    public CloudUser querySimpleCloudUser(CloudUser user);
    /**
     * @Description: 获取用户数量
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/30
     */
    public Integer queryUserCount();
}
