package cn.objectspace.componentcenter.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 服务器执行命令记录
 * @Author: NoCortY
 * @Date: 2020/4/16
 */
public class CloudServerCommandExecuteRecord implements Serializable {
    private static final long serialVersionUID = 6603848022542906614L;
    private Integer id;
    private String serverIp;
    private String result;
    private String command;
    private Integer executeUserId;
    private String executeUserEmail;
    private String executeUserName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date executeDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getExecuteUserId() {
        return executeUserId;
    }

    public void setExecuteUserId(Integer executeUserId) {
        this.executeUserId = executeUserId;
    }

    public String getExecuteUserEmail() {
        return executeUserEmail;
    }

    public void setExecuteUserEmail(String executeUserEmail) {
        this.executeUserEmail = executeUserEmail;
    }

    public String getExecuteUserName() {
        return executeUserName;
    }

    public void setExecuteUserName(String executeUserName) {
        this.executeUserName = executeUserName;
    }

    public Date getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(Date executeDate) {
        this.executeDate = executeDate;
    }
}
