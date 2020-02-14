package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

public class CloudServerDto implements Serializable {
    private static final long serialVersionUID = 3164258582713393035L;
    private String serverIp;
    private String serverName;
    private String serverOsType;
    private String serverOsVersion;
    private Boolean isMonitor;

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

    public Boolean getMonitor() {
        return isMonitor;
    }

    public void setMonitor(Boolean monitor) {
        isMonitor = monitor;
    }
}
