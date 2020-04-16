package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

/**
 * @Description: 常用命令DTO
 * @Author: NoCortY
 * @Date: 2020/4/16
 */
public class SimpleCommandDto implements Serializable {
    private static final long serialVersionUID = 4856561028290680738L;

    private String commandName;
    private String commandContent;
    private String createUserName;

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

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
