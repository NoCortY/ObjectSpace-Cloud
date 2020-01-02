package cn.objectspace.componentcenter.pojo.entity;

import java.io.Serializable;

public class CloudMicroService implements Serializable {
    private static final long serialVersionUID = -4544829776702439771L;
    private Integer applicationId;
    private String applicationName;
    private String applicationPassword;
    private String applicationJarName;
    private String applicationLocation;
    private String applicationStarter;
    private String applicationServerIp;
    private String applicationPort;
    private String applicationDesc;


    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationPassword() {
        return applicationPassword;
    }

    public void setApplicationPassword(String applicationPassword) {
        this.applicationPassword = applicationPassword;
    }

    public String getApplicationJarName() {
        return applicationJarName;
    }

    public void setApplicationJarName(String applicationJarName) {
        this.applicationJarName = applicationJarName;
    }

    public String getApplicationLocation() {
        return applicationLocation;
    }

    public void setApplicationLocation(String applicationLocation) {
        this.applicationLocation = applicationLocation;
    }

    public String getApplicationStarter() {
        return applicationStarter;
    }

    public void setApplicationStarter(String applicationStarter) {
        this.applicationStarter = applicationStarter;
    }

    public String getApplicationServerIp() {
        return applicationServerIp;
    }

    public void setApplicationServerIp(String applicationServerIp) {
        this.applicationServerIp = applicationServerIp;
    }

    public String getApplicationPort() {
        return applicationPort;
    }

    public void setApplicationPort(String applicationPort) {
        this.applicationPort = applicationPort;
    }

    public String getApplicationDesc() {
        return applicationDesc;
    }

    public void setApplicationDesc(String applicationDesc) {
        this.applicationDesc = applicationDesc;
    }
}
