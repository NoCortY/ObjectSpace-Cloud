package cn.objectspace.authcenter.dao;

import cn.objectspace.authcenter.pojo.dto.URPDto;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import cn.objectspace.authcenter.pojo.entity.UrlFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @Description: Shiro持久层
* @Author: NoCortY
* @Date: 2019/12/19
*/
@Mapper
public interface ShiroDao {
    /**
     * @Description: 获取Shiro需要的初始化过滤器链
     * @Param:
     * @return: 过滤器列表
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    public List<UrlFilter> queryAllUrlFilter();

    /**
     * @Description: 通过用户邮箱查询用户信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public CloudUser queryCloudUserByUserEmail(@Param("userEmail") String userEmail);

    /**
     * @Description:  查询用户角色权限对应关系
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public URPDto queryURPByUserEmail(@Param("userEmail") String userEmail,@Param("applicationId")Integer applicationId);

    /**
     * @Description: 插入数据
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public Integer insertCloudUser(CloudUser cloudUser);
}
