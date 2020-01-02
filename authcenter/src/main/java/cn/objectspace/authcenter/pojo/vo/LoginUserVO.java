package cn.objectspace.authcenter.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
* @Description: 用户登录VO
* @Author: NoCortY
* @Date: 2019/12/22
*/
@ApiModel(value="LoginUserVO",description = "登录请求模型")
public class LoginUserVO {
    @ApiModelProperty(value = "用户邮箱",required = true)
    @NotNull(message = "邮箱不能为空")
    private String userEmail;
    @ApiModelProperty(value="用户密码",required = true)
    @NotNull(message = "密码不能为空")
    private String userPassword;

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
}
