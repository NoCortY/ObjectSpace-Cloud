package cn.objectspace.authcenter.service;

/**
* @Description: 用户中心职能
* @Author: NoCortY
* @Date: 2019/12/17
*/
public interface UserService {
    public Boolean sendEmail(String toEmail);
    public Boolean checkVerifyCode(String userEmail,String verifyCode);
    public Integer getUserCount();
}
