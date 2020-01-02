package cn.objectspace.authcenter.service.impl;

import cn.objectspace.authcenter.dao.CloudUserDao;
import cn.objectspace.authcenter.service.UserService;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
* @Description: 用户中心职能
* @Author: NoCortY
* @Date: 2019/12/19
*/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private CloudUserDao cloudUserDao;
    @Autowired
    private JavaMailSender mailSender;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    RedisUtil redisUtil = new RedisUtil();
    @Override
    public Boolean sendEmail(String toEmail) {
        int randomCode = new Random().nextInt(999999);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(ConstantPool.Shiro.FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject("ObjectSpace云组件平台-用户认证授权中心");
        message.setText("您好，您的验证码为:"+randomCode+"。提示：注意保管，请勿告诉他人，验证码有效期为15分钟。");
        try{
            mailSender.send(message);
        }catch (Exception e){
            logger.error("发送邮箱验证码异常");
            logger.error("异常信息:{}",e.getMessage());
            return false;
        }
        //验证码有效期为15分钟
        redisUtil.set(ConstantPool.Shiro.VERIFY_KEY+toEmail,String.valueOf(randomCode),900);
        logger.info("验证码发送成功!");
        return true;
    }

    /**
     * @Description: 检查验证码是否正确
     * @Param: [userEmail, verifyCode]
     * @return: java.lang.Boolean
     * @Author: NoCortY
     * @Date: 2019/12/26
     */
    @Override
    public Boolean checkVerifyCode(String userEmail, String verifyCode) {
        String rightCode = redisUtil.get(ConstantPool.Shiro.VERIFY_KEY+userEmail);
        if(rightCode!=null&&rightCode.equals(verifyCode)){
            redisUtil.del(ConstantPool.Shiro.VERIFY_KEY+userEmail);
            return true;
        }else
            return false;
    }


    /**
     * @Description: 获取用户数量
     * @Param: []
     * @return: java.lang.Integer
     * @Author: NoCortY
     * @Date: 2019/12/30
     */
    @Override
    public Integer getUserCount() {
        return cloudUserDao.queryUserCount();
    }
}
