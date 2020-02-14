package cn.objectspace.componentcenter.pojo.entity;

import java.io.Serializable;

public class CloudServer implements Serializable {
    private static final long serialVersionUID = 1996457269403152119L;
    private String serverIp;
    private String serverName;
    private String serverOsType;
    private String serverOsVersion;
    private Boolean isMonitor;
    private Integer serverUser;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public Boolean getIsMonitor() {
        return this.isMonitor;
    }

    public void setIsMonitor(Boolean isMonitor) {
        this.isMonitor = isMonitor;
    }

    public Integer getServerUser() {
        return serverUser;
    }

    public void setServerUser(Integer serverUser) {
        this.serverUser = serverUser;
    }

    @Override
    public String toString() {
        return "CloudServer{" +
                "serverIp='" + serverIp + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverOsType='" + serverOsType + '\'' +
                ", serverOsVersion='" + serverOsVersion + '\'' +
                ", isMonitor=" + isMonitor +
                ", serverUser=" + serverUser +
                '}';
    }
}
