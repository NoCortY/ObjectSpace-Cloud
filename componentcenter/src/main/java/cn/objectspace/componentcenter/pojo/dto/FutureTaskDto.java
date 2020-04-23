package cn.objectspace.componentcenter.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 计划任务列表DTO
 * @Author: NoCortY
 * @Date: 2020/4/23
 */
public class FutureTaskDto implements Serializable {
    private static final long serialVersionUID = -57297217377867454L;

    private String taskName;
    private boolean isShellScript;
    private String shellScriptPath;
    private String shellCommand;
    private String executeServerIp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date executeTime;
    private String createUserName;
    private Integer executed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date executedTime;
    private String executedDesc;
    private String executedResult;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isShellScript() {
        return isShellScript;
    }

    public void setIsShellScript(boolean shellScript) {
        isShellScript = shellScript;
    }

    public String getShellScriptPath() {
        return shellScriptPath;
    }

    public void setShellScriptPath(String shellScriptPath) {
        this.shellScriptPath = shellScriptPath;
    }

    public String getShellCommand() {
        return shellCommand;
    }

    public void setShellCommand(String shellCommand) {
        this.shellCommand = shellCommand;
    }

    public String getExecuteServerIp() {
        return executeServerIp;
    }

    public void setExecuteServerIp(String executeServerIp) {
        this.executeServerIp = executeServerIp;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getExecuted() {
        return executed;
    }

    public void setExecuted(Integer executed) {
        this.executed = executed;
    }

    public Date getExecutedTime() {
        return executedTime;
    }

    public void setExecutedTime(Date executedTime) {
        this.executedTime = executedTime;
    }

    public String getExecutedDesc() {
        return executedDesc;
    }

    public void setExecutedDesc(String executedDesc) {
        this.executedDesc = executedDesc;
    }

    public String getExecutedResult() {
        return executedResult;
    }

    public void setExecutedResult(String executedResult) {
        this.executedResult = executedResult;
    }
}

