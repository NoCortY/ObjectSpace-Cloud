package cn.objectspace.componentcenter.pojo.entity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Description: 存储ssh连接相关信息的实体类
 * @Author: NoCortY
 * @Date: 2020/3/7
 */
public class CloudSSH {
    private WebSocketSession webSocketSession;
    private JSch jSch;
    private Channel channel;


    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public JSch getjSch() {
        return jSch;
    }

    public void setjSch(JSch jSch) {
        this.jSch = jSch;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
