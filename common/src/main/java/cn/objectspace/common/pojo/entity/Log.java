package cn.objectspace.common.pojo.entity;

import java.io.Serializable;
import java.util.Date;

public class Log implements Serializable {
    private static final long serialVersionUID = -2470126945279603107L;
    private Integer id;
    private Date operateDate;
    private String inputParameter;
    private String outputParameter;
    private String operateInterface;
    private String operateUserIp;
    private Integer logApplication;

    public Log(){}
    public Log(Date operateDate, String inputParameter, String outputParameter, String operateInterface, String operateUserIp,Integer logApplication) {
        this.operateDate = operateDate;
        this.inputParameter = inputParameter;
        this.outputParameter = outputParameter;
        this.operateInterface = operateInterface;
        this.operateUserIp = operateUserIp;
        this.logApplication = logApplication;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public String getInputParameter() {
        return inputParameter;
    }

    public void setInputParameter(String inputParameter) {
        this.inputParameter = inputParameter;
    }

    public String getOutputParameter() {
        return outputParameter;
    }

    public void setOutputParameter(String outputParameter) {
        this.outputParameter = outputParameter;
    }

    public String getOperateInterface() {
        return operateInterface;
    }

    public void setOperateInterface(String operateInterface) {
        this.operateInterface = operateInterface;
    }

    public String getOperateUserIp() {
        return operateUserIp;
    }

    public void setOperateUserIp(String operateUserIp) {
        this.operateUserIp = operateUserIp;
    }

    public Integer getLogApplication() {
        return logApplication;
    }

    public void setLogApplication(Integer logApplication) {
        this.logApplication = logApplication;
    }
}
