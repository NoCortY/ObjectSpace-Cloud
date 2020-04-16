package cn.objectspace.componentcenter.pojo.entity;

import java.io.Serializable;

/**
 * @Description: 常用命令PO
 * @Author: NoCortY
 * @Date: 2020/4/16
 */
public class SimpleCommand implements Serializable {
    private static final long serialVersionUID = 1416964983611462340L;
    private Integer id;
    private String commandName;
    private String commandContent;
    private Integer createUserId;
    private String createUserEmail;
    private String createUserName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandContent() {
        return commandContent;
    }

    public void setCommandContent(String commandContent) {
        this.commandContent = commandContent;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserEmail() {
        return createUserEmail;
    }

    public void setCreateUserEmail(String createUserEmail) {
        this.createUserEmail = createUserEmail;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
