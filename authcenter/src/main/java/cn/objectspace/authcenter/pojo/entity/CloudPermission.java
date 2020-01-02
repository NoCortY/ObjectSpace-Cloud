package cn.objectspace.authcenter.pojo.entity;

import java.io.Serializable;
import java.util.Date;

public class CloudPermission implements Serializable {

    private static final long serialVersionUID = -1086929199337570785L;
    private Integer permissionId;
    private String permissionName;
    private String permissionUrl;
    private String permissionDesc;
    private Date createDate;
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

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPermissionApplication() {
        return permissionApplication;
    }

    public void setPermissionApplication(String permissionApplication) {
        this.permissionApplication = permissionApplication;
    }
}
