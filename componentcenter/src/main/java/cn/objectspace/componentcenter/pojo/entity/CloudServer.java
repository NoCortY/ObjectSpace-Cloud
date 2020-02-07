package cn.objectspace.componentcenter.pojo.entity;

import java.io.Serializable;

public class CloudServer implements Serializable {
    private static final long serialVersionUID = 1996457269403152119L;
    private String serverIp;
    private String serverName;
    //是否被监控 1 是 0 否
    private Boolean isMonitor;
    //生产/测试域 1/0
    private Boolean land;
    private String serverDesc;
    //服务器归属用户
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

    public Boolean getMonitor() {
        return isMonitor;
    }

    public void setMonitor(Boolean monitor) {
        isMonitor = monitor;
    }

    public Boolean getLand() {
        return land;
    }

    public void setLand(Boolean land) {
        this.land = land;
    }

    public String getServerDesc() {
        return serverDesc;
    }

    public void setServerDesc(String serverDesc) {
        this.serverDesc = serverDesc;
    }

    public Integer getServerUser() {
        return serverUser;
    }

    public void setServerUser(Integer serverUser) {
        this.serverUser = serverUser;
    }
}
