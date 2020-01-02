package cn.objectspace.authcenter.pojo.entity;

import java.io.Serializable;

/**
* @Description: 角色
* @Author: NoCortY
* @Date: 2019/12/16
*/
public class CloudRole implements Serializable {
    private static final long serialVersionUID = 213507918615948814L;
    private Integer roleId;
    private String roleName;
    private String roleDesc;
    private String roleCreateDate;
    private String roleApplication;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public String getRoleCreateDate() {
        return roleCreateDate;
    }

    public void setRoleCreateDate(String roleCreateDate) {
        this.roleCreateDate = roleCreateDate;
    }

    public String getRoleApplication() {
        return roleApplication;
    }

    public void setRoleApplication(String roleApplication) {
        this.roleApplication = roleApplication;
    }

    @Override
    public String toString() {
        return "CloudRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleDesc='" + roleDesc + '\'' +
                ", roleCreateDate='" + roleCreateDate + '\'' +
                ", roleApplication='" + roleApplication + '\'' +
                '}';
    }
}
