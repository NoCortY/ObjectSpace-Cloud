package cn.objectspace.authcenter.pojo.dto;

import java.io.Serializable;
/**
* @Description: 认证DTO
* @Author: NoCortY
* @Date: 2019/12/19
*/
public class AuthDto implements Serializable {
    private static final long serialVersionUID = 5694314446529595772L;
    private String authCode;
    private String authMessage;
    private String userEmail;
    private String token;
    private  String uuid;
    /**
     * @Description:接返回失败信息
     * @Param: [authCode, authMessage, userEmail]
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public AuthDto(String authCode, String authMessage, String userEmail) {
        this.authCode = authCode;
        this.authMessage = authMessage;
        this.userEmail = userEmail;
    }

    /**
     * @Description: 成功 生成令牌或uuid返回
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    public AuthDto(String authCode, String authMessage, String userEmail, String token,String uuid) {
        this.authCode = authCode;
        this.authMessage = authMessage;
        this.userEmail = userEmail;
        this.token = token;
        this.uuid = uuid;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAuthMessage() {
        return authMessage;
    }

    public void setAuthMessage(String authMessage) {
        this.authMessage = authMessage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
