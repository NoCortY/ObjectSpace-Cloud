package cn.objectspace.authcenter.service;

import cn.objectspace.authcenter.pojo.dto.AuthDto;
import cn.objectspace.authcenter.pojo.dto.URPDto;
import cn.objectspace.authcenter.pojo.entity.CloudUser;

/**
* @Description: 认证中心职能
* @Author: NoCortY
* @Date: 2019/12/16
*/
public interface ACService {
    /**
     * @Description: 登录
     * @Param: cloudUser
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public AuthDto userLogin(CloudUser currentUser);
    /**
     * @Description: 注册
     * @Param: cloudUser
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public AuthDto userRegister(CloudUser cloudUser);
    /**
     * @Description: 授权
     * @Param: cloudUser
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public URPDto authorizationInfo(String token,Integer applicationId);
    /**
     * @Description: 认证
     * @Param: cloudUser
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public AuthDto authenticationInfo(String uuid);

    public void tokenDestroy(String token);
}
