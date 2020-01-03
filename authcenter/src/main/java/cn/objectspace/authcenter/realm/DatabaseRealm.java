package cn.objectspace.authcenter.realm;

import cn.objectspace.authcenter.dao.ShiroDao;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseRealm extends AuthorizingRealm {
    @Autowired
    private ShiroDao shiroDao;
    private Logger logger = LoggerFactory.getLogger(DatabaseRealm.class);
    /**
     * @Description: 用户授权
     * @Param: [principals]
     * @return: org.apache.shiro.authz.AuthorizationInfo
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * @Description: 用户认证
     * @Param: [token]
     * @return: org.apache.shiro.authc.AuthenticationInfo
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        //数据库中的用户密码和盐
        String passwordInDB = null;
        String userSalt = null;

        //使用邮箱作为用户名登录
        String userEmail = t.getUsername();
        if(userEmail==null||"".equals(userEmail)) {
            logger.error("未知的账号");
            throw new UnknownAccountException();
        }
        CloudUser cloudUser = null;
        try{
            cloudUser = shiroDao.queryCloudUserByUserEmail(userEmail);
        }catch (Exception e){
            logger.error("认证失败");
            logger.error("异常信息:"+e.getMessage());
            return null;
        }

        if(cloudUser==null) throw new UnknownAccountException();

        if(!cloudUser.getUserStatus()){
            logger.error("账号:"+cloudUser.getUserEmail()+"已被禁止登录");
            throw new DisabledAccountException();
        }
        passwordInDB = cloudUser.getUserPassword();
        userSalt = cloudUser.getUserSalt();
        //认证
        SimpleAuthenticationInfo ac =new SimpleAuthenticationInfo(userEmail,passwordInDB, ByteSource.Util.bytes(userSalt),getName());
        return ac;
    }
}
