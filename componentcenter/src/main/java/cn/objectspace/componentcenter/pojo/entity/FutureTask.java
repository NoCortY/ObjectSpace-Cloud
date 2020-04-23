package cn.objectspace.componentcenter.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class FutureTask implements Serializable {
    private static final long serialVersionUID = 5892898574813519402L;

    private Integer id;
    private String taskName;
    private boolean isShellScript;
    private String shellScriptPath;
    private String shellCommand;
    private String executeServerIp;
    private Integer createUserId;
    private String createUserName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date executeTime;
    private Integer executed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date executedTime;
    private String executedDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean getIsShellScript() {
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

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
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

    public boolean isShellScript() {
        return isShellScript;
    }

    public void setShellScript(boolean shellScript) {
        isShellScript = shellScript;
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

    @Override
    public String toString() {
        return "FutureTask{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", isShellScript=" + isShellScript +
                ", shellScriptPath='" + shellScriptPath + '\'' +
                ", shellCommand='" + shellCommand + '\'' +
                ", executeServerIp='" + executeServerIp + '\'' +
                ", createUserId=" + createUserId +
                ", createUserName='" + createUserName + '\'' +
                ", executeTime=" + executeTime +
                ", executed=" + executed +
                ", executedTime=" + executedTime +
                ", executedDesc='" + executedDesc + '\'' +
                '}';
    }
}
