package cn.objectspace.componentcenter.pojo.entity;

import java.io.Serializable;

public class CloudServer implements Serializable {
    private static final long serialVersionUID = 1996457269403152119L;
    private String serverIp;
    private String serverName;
    private String daemonLocation;

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

    public String getDaemonLocation() {
        return daemonLocation;
    }

    public void setDaemonLocation(String daemonLocation) {
        this.daemonLocation = daemonLocation;
    }
}
