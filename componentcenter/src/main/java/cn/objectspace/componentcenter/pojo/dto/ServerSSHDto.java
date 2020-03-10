package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

/**
 * @Description: 服务器SSH账户管理页面的信息
 * @Author: NoCortY
 * @Date: 2020/3/9
 */
public class ServerSSHDto implements Serializable {
    private static final long serialVersionUID = -7865332256200841617L;
    private String serverIp;
    private String serverOsType;
    private String serverOsVersion;
    private String serverStatus;
    private String serverDesc;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerOsType() {
        return serverOsType;
    }

    public void setServerOsType(String serverOsType) {
        this.serverOsType = serverOsType;
    }

    public String getServerOsVersion() {
        return serverOsVersion;
    }

    public void setServerOsVersion(String serverOsVersion) {
        this.serverOsVersion = serverOsVersion;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getServerDesc() {
        return serverDesc;
    }

    public void setServerDesc(String serverDesc) {
        this.serverDesc = serverDesc;
    }

    @Override
    public String toString() {
        return "ServerSSHDto{" +
                "serverIp='" + serverIp + '\'' +
                ", serverOsType='" + serverOsType + '\'' +
                ", serverOsVersion='" + serverOsVersion + '\'' +
                ", serverStatus='" + serverStatus + '\'' +
                '}';
    }
}
