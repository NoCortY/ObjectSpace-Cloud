package cn.objectspace.componentcenter.pojo.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 服务器运行事件记录
 * @Author: NoCortY
 * @Date: 2020/4/22
 */
public class ServerRuntimeRecord implements Serializable {
    private static final long serialVersionUID = 7545783441164400811L;
    private Integer id;
    private String serverIp;
    private Integer userId;
    private String userEmail;
    private Integer serverState;
    private Date recordTime;
    private String recordContent;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getServerState() {
        return serverState;
    }

    public void setServerState(Integer serverState) {
        this.serverState = serverState;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public String getRecordContent() {
        return recordContent;
    }

    public void setRecordContent(String recordContent) {
        this.recordContent = recordContent;
    }
}
