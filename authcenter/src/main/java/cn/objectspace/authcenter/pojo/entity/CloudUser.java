package cn.objectspace.authcenter.pojo.entity;

import java.io.Serializable;
import java.util.Date;

/**
* @Description: 用户实体
* @Author: NoCortY
* @Date: 2019/12/16
*/
public class CloudUser implements Serializable {
    private static final long serialVersionUID = -8056838638678482282L;
    private Integer userId;
    private String userEmail;
    private String userPassword;
    private String userSalt;
    private String userName;
    private String userProfile;
    private Boolean userStatus;
    private Date userRegisterDate;
    private Date userLastLoginDate;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public String getUserSalt() {
        return userSalt;
    }

    public void setUserSalt(String userSalt) {
        this.userSalt = userSalt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Date getUserRegisterDate() {
        return userRegisterDate;
    }

    public void setUserRegisterDate(Date userRegisterDate) {
        this.userRegisterDate = userRegisterDate;
    }

    public Date getUserLastLoginDate() {
        return userLastLoginDate;
    }

    public void setUserLastLoginDate(Date userLastLoginDate) {
        this.userLastLoginDate = userLastLoginDate;
    }

    @Override
    public String toString() {
        return "CloudUser{" +
                "userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userSalt='" + userSalt + '\'' +
                ", userName='" + userName + '\'' +
                ", userProfile='" + userProfile + '\'' +
                ", userStatus=" + userStatus +
                ", userRegisterDate=" + userRegisterDate +
                ", userLastLoginDate=" + userLastLoginDate +
                '}';
    }
}
