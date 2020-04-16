package cn.objectspace.common.pojo.entity;

import java.io.Serializable;
import java.util.List;

/**
* @Description: 用户、角色、权限 DTO
* @Author: NoCortY
* @Date: 2019/12/16
*/
public class URPDto implements Serializable {
    private static final long serialVersionUID = -6115496718285680728L;
    /**************用户***************/
    private Integer userId;
    private String userEmail;
    private String userName;
    private String userStatus;
    /********************************/

    /**************角色***************/
    List<CloudRoleDto> roleList;
    /********************************/

    /**************权限**************/
    List<CloudPermissionDto> cloudPermissionList;
    /*******************************/

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public List<CloudRoleDto> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<CloudRoleDto> roleList) {
        this.roleList = roleList;
    }

    public List<CloudPermissionDto> getCloudPermissionList() {
        return cloudPermissionList;
    }

    public void setCloudPermissionList(List<CloudPermissionDto> cloudPermissionList) {
        this.cloudPermissionList = cloudPermissionList;
    }
}
