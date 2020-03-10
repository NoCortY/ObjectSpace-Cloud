package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

public class ServerSSHInfoDto implements Serializable {
    private static final long serialVersionUID = -4053724952954869946L;
    private String sshUser;
    private String sshPassword;
    private String sshPort = "22";

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
        this.sshPort = sshPort;
    }
}
