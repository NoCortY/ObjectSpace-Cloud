package cn.objectspace.authcenter.service.impl;

import cn.objectspace.authcenter.dao.CloudUserDao;
import cn.objectspace.authcenter.dao.ShiroDao;
import cn.objectspace.authcenter.pojo.dto.AuthDto;
import cn.objectspace.authcenter.pojo.dto.URPDto;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import cn.objectspace.authcenter.service.ACService;
import cn.objectspace.authcenter.util.TokenUtil;
import cn.objectspace.authcenter.util.UUIDUtil;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;


/**
* @Description: 认证中心职能
* @Author: NoCortY
* @Date: 2019/12/19
*/
@Service
public class ACServiceImpl implements ACService {
    @Autowired
    private ShiroDao shiroDao;
    @Autowired
    private CloudUserDao cloudUserDao;
    @Autowired
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(ACServiceImpl.class);
    /**
     * @Description: 单点登录
     * @Param: [cloudUser]
     * @return: cn.objectspace.authcenter.pojo.dto.AuthDto
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    @Override
    public AuthDto userLogin(CloudUser currentUser) {
        AuthDto authDto = null;
        if(currentUser==null){
            //用户为空则直接登录失败
            authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,null);
            logger.info("当前用户为空");
            return authDto;
        }
        //获取当前用户信息
        Subject subject = SecurityUtils.getSubject();
        CloudUser cloudUser = null;
        UsernamePasswordToken token = new UsernamePasswordToken(currentUser.getUserEmail(),currentUser.getUserPassword());
        try{
            //验证令牌
            subject.login(token);
        }catch (UnknownAccountException e){
            logger.info("用户不存在");
            authDto = new AuthDto(ConstantPool.Shiro.AC_UKNOWN_ACCOUNT,ConstantPool.Shiro.AC_UKNOWN_ACCOUNT_MESSAGE,currentUser.getUserEmail());
            return authDto;
        }catch (DisabledAccountException e) {
            logger.info("用户:{}已是封禁用户，但在尝试登录",currentUser.getUserEmail());
            authDto = new AuthDto(ConstantPool.Shiro.AC_BINDED_CODE,ConstantPool.Shiro.AC_BINDED_MESSAGE,currentUser.getUserEmail());
            return authDto;
        } catch (AuthenticationException e) {
            logger.info("用户:{}认证失败",currentUser.getUserEmail());
            authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,currentUser.getUserEmail());
            return authDto;
        }
        //登录成功
        Session currentSession = subject.getSession();
        String uuid = UUIDUtil.getUUID();
        authDto = new AuthDto(ConstantPool.Shiro.AC_SUCCESS_CODE,ConstantPool.Shiro.AC_SUCCESS_MESSAGE,currentUser.getUserEmail(),null,uuid);
        //将用户信息放入redis中 并存入session
        cloudUser = cloudUserDao.querySimpleCloudUser(currentUser);
        currentSession.setAttribute(uuid,SerializeUtil.serialize(cloudUser));
        redisUtil.set(SerializeUtil.serialize(uuid), SerializeUtil.serialize(cloudUser),1800);
        return authDto;
    }

    /**
     * @Description: 注册
     * @Param: [cloudUser]
     * @return: cn.objectspace.authcenter.pojo.dto.AuthDto
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @Transactional
    @Override
    public AuthDto userRegister(CloudUser cloudUser) {
        AuthDto authDto = null;
        if(cloudUser==null|| "".equals(cloudUser.getUserEmail())||"".equals(cloudUser.getUserPassword())){
            authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,null);
            logger.info("当前用户某些属性为空（邮箱、密码、用户归属应用），无法注册");
            return authDto;
        }
        //设置账号正常
        cloudUser.setUserStatus(true);
        //设置账号注册时间
        cloudUser.setUserRegisterDate(new Date());
        //盐
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        //密码明文
        String password = cloudUser.getUserPassword();
        //加密
        String encodedPassword =new SimpleHash(ConstantPool.Shiro.ALGORITHM_NAME,password,salt,ConstantPool.Shiro.ITERATIONS).toString();
        cloudUser.setUserPassword(encodedPassword);
        cloudUser.setUserSalt(salt);

        int effectiveNums = 0;
        try{
            effectiveNums = shiroDao.insertCloudUser(cloudUser);
            shiroDao.insertUserRole(cloudUser.getUserId(),ConstantPool.Shiro.GENERAL_USER_ROLE_ID);
        } catch (Exception e){
            logger.error("注册异常");
            logger.error("异常信息:{}",e.getMessage());
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,cloudUser.getUserEmail());
            return authDto;
        }
        if(effectiveNums>0){
            authDto = new AuthDto(ConstantPool.Shiro.AC_SUCCESS_CODE,ConstantPool.Shiro.AC_SUCCESS_MESSAGE,cloudUser.getUserEmail());
        }else{
            authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,null);
        }
        return authDto;
    }

    /**
     * @Description: 用户授权
     * @Param: [token, applicationId]
     * @return: cn.objectspace.authcenter.pojo.dto.URPDto
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @Override
    public URPDto authorizationInfo(String token,Integer applicationId) {
        if(token==null||applicationId==null){
            logger.error("token或applicationId为空");
            return null;
        }
        URPDto urpDto = null;
        CloudUser cloudUser = null;
        //先从Redis中拿
        cloudUser = (CloudUser) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(token)));
        if(cloudUser!=null){
            try{
                urpDto = shiroDao.queryURPByUserEmail(cloudUser.getUserEmail(),applicationId);
            }catch (Exception e){
                logger.error("尝试授权失败");
                logger.error("异常信息:{}",e.getMessage());
                return null;
            }
            //使用过的令牌失效
            redisUtil.del(SerializeUtil.serialize(token));
        }else{
            //如果此时用户为null，那么说明要么是redis down了，要么是该用户本就是不合法用户，伪造了token，或者是Redis中的token过期了。
            //那么就从session中拿
            Session session = SecurityUtils.getSubject().getSession();
            cloudUser = (CloudUser) SerializeUtil.unSerialize((byte[]) session.getAttribute(token));
            //拿完如果是null，直接就是非法用户，如果不是null，那么说明是合法用户，要让这个token失效。
            if(cloudUser==null){
                return null;
            }else {
                try{
                    urpDto = shiroDao.queryURPByUserEmail(cloudUser.getUserEmail(),applicationId);
                }catch (Exception e){
                    logger.error("尝试授权失败");
                    logger.error("异常信息:{}",e.getMessage());
                    return null;
                }
                //失效
                session.removeAttribute(token);
            }
        }
        return urpDto;
    }

    /**
     * @Description: 认证
     * @Param: [uuid]
     * @return: cn.objectspace.authcenter.pojo.dto.AuthDto
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @Override
    public AuthDto authenticationInfo(String uuid) {
        AuthDto authDto = null;
        if(uuid==null||"".equals(uuid)){
            //判空
            authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,null,null,null);
            return authDto;
        }
        CloudUser cloudUser = (CloudUser) SerializeUtil.unSerialize(redisUtil.get(SerializeUtil.serialize(uuid)));
        if(cloudUser==null){
            //如果从redis中无法获取该用户，说明redis有可能down了，那么直接尝试从session中拿，也有可能是Redis中的uuid过期了。
            Session session = SecurityUtils.getSubject().getSession();
            cloudUser = (CloudUser) SerializeUtil.unSerialize((byte[]) session.getAttribute(uuid));
            if(cloudUser!=null){
                //如果从session中得到了该用户的信息，说明已经登录，直接生成Token然后放入session中(因为redis已经down了)，并且尝试放入Redis中（续约操作）
                String token = TokenUtil.getInstance().generateTokeCode();
                session.setAttribute(token,SerializeUtil.serialize(cloudUser));
                //续约
                redisUtil.set(SerializeUtil.serialize(token), SerializeUtil.serialize(cloudUser), 30);
                authDto = new AuthDto(ConstantPool.Shiro.AC_SUCCESS_CODE,ConstantPool.Shiro.AC_SUCCESS_MESSAGE,cloudUser.getUserEmail(),token,null);
                return authDto;
            }
        }else{
            //token放入Redis
            String token = TokenUtil.getInstance().generateTokeCode();
            //token有效期15秒，用完即作废，暂时方案。正常应该是访问AC服务也会消耗token(暂时并存)
            //如果外部服务没有进行消费的token，直接通过销毁程序进行删除
            redisUtil.set(SerializeUtil.serialize(token), SerializeUtil.serialize(cloudUser), 30);
            //每次认证都需要续约一次，保证用户在使用期间不会断开
            redisUtil.expire(SerializeUtil.serialize(uuid), 1800);
            authDto = new AuthDto(ConstantPool.Shiro.AC_SUCCESS_CODE,ConstantPool.Shiro.AC_SUCCESS_MESSAGE,cloudUser.getUserEmail(),token,null);
            return authDto;
        }
        //如果依旧为null
        authDto = new AuthDto(ConstantPool.Shiro.AC_FAILURE_CODE,ConstantPool.Shiro.AC_FAILURE_MESSAGE,null,null,null);
        return authDto;
    }

    /**
     * @Description: 销毁token, 如果没有用到token，那么直接通过这里进行销毁
     * @Param: [token]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/15
     */
    @Override
    public void tokenDestroy(String token) {
        if (token != null) {
            redisUtil.del(SerializeUtil.serialize(token));
        }
    }
}
