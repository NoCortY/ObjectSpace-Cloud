package cn.objectspace.authcenter.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
* @Description: 注册用户VO
* @Author: NoCortY
* @Date: 2019/12/22
*/
@ApiModel(value="RegisterUserVO",description = "注册请求模型")
public class RegisterUserVO {
    @ApiModelProperty(value="用户邮箱",required = true)
    @Email(message = "请输入正确的邮箱")
    private String userEmail;
    @ApiModelProperty(value="用户密码",required = true)
    @NotNull(message = "密码不能为空")
    private String userPassword;
    @ApiModelProperty(value="用户昵称")
    private String userName;
    @ApiModelProperty(value="验证码")
    private String emailVerifyCode;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailVerifyCode() {
        return emailVerifyCode;
    }

    public void setEmailVerifyCode(String emailVerifyCode) {
        this.emailVerifyCode = emailVerifyCode;
    }
}
