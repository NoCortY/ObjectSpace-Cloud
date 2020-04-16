package cn.objectspace.logcenter.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
* @Description: 日志实体
* @Author: NoCortY
* @Date: 2019/12/19
*/
public class Log implements Serializable {
    private static final long serialVersionUID = 9210955183098226916L;
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateDate;
    private String inputParameter;
    private String outputParameter;
    private String operateInterface;
    private String operateUserIp;
    private Integer logApplication;
    public Log(){}
    public Log(Date operateDate, String inputParameter, String outputParameter, String operateInterface, String operateUserIp,Integer applicationId) {
        this.operateDate = operateDate;
        this.inputParameter = inputParameter;
        this.outputParameter = outputParameter;
        this.operateInterface = operateInterface;
        this.operateUserIp = operateUserIp;
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
