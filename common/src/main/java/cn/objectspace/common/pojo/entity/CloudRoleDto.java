package cn.objectspace.common.pojo.entity;

import java.io.Serializable;

/**
* @Description: 角色DTO
* @Author: NoCortY
* @Date: 2019/12/17
*/
public class CloudRoleDto implements Serializable {
    private static final long serialVersionUID = -1278068366630474846L;
    private Integer roleId;
    private String roleName;
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

    public String getRoleApplication() {
        return roleApplication;
    }

    public void setRoleApplication(String roleApplication) {
        this.roleApplication = roleApplication;
    }
}
