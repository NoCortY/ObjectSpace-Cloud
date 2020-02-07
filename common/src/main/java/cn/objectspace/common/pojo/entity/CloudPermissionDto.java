package cn.objectspace.common.pojo.entity;

import java.io.Serializable;

/**
* @Description: 权限DTO
* @Author: NoCortY
* @Date: 2019/12/17
*/
public class CloudPermissionDto implements Serializable {
    private static final long serialVersionUID = 4410795217917408734L;
    private Integer permissionId;
    private String permissionName;
    private String permissionUrl;
    private String permissionApplication;

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionUrl() {
        return permissionUrl;
    }

    public void setPermissionUrl(String permissionUrl) {
        this.permissionUrl = permissionUrl;
    }

    public String getPermissionApplication() {
        return permissionApplication;
    }

    public void setPermissionApplication(String permissionApplication) {
        this.permissionApplication = permissionApplication;
    }
}
