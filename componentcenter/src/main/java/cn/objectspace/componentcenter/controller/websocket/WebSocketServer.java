package cn.objectspace.componentcenter.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
* @Description: WebSocket服务器
* @Author: NoCortY
* @Date: 2020/2/8
*/
@ServerEndpoint("/WebSocketServer/{serverIP}")
@Component
public class WebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    /**
     * 静态变量 用于记录当前在线连接数
     */
    private static Integer onlineCount = 0;
    /**
     * concurrent包下的线程安全HashMap，用来存放客户端对应的WebSocket对象
     */
    private static ConcurrentHashMap<String,WebSocketServer> webSocketServerConcurrentHashMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收服务器IP
     */
    private String userId = "";

    /**
     * @Description: 服务器与websocket建立通讯后回调方法
     * @Param: [session, serverIP]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId")String userId){
        this.session = session;
        this.userId = userId;
        if(webSocketServerConcurrentHashMap.containsKey(userId)){
            webSocketServerConcurrentHashMap.remove(userId);
            //加入hashmap
            webSocketServerConcurrentHashMap.put(userId,this);
        }else{
            webSocketServerConcurrentHashMap.put(userId,this);
            //在线人数数量+1
            addOnlineCount();
        }
        logger.info("用户:{}与WebSocket建立通讯,当前在线用户数量为:{}人",userId,WebSocketServer.onlineCount);
    }

    @OnMessage
    public void onMessage(String message,Session session){
        /*logger.info("收到消息:{}",message);
        //可以群发消息
        //消息也可以保存到数据库，redis
        if(StringUtils.isNotBlank(message)){
            //如果消息不为空，则解析发送的报文
            try{
                ObjectMapper objectMapper = new ObjectMapper();

            }
        }*/
    }
    /**
     * @Description: 实现服务器向客户端推送消息
     * @Param: [message]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    public void sendMessage(Object message) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        this.session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
    }

    /**
     * @Description: 推送任意消息给用户
     * @Param: [message, userId]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    public static void sendInfo(Object message,@PathParam("userId")String userId) throws IOException {
        if(!webSocketServerConcurrentHashMap.containsKey(userId)){
            //可以改为暂时存入消息队列等待用户在线后调用OnOpen方法时一口气发送出去
            logger.info("当前用户不在线，无法发送");
            return;
        }
        if(StringUtils.isNotBlank(userId)){
            logger.info("发送消息到用户ID：{},消息:{}",userId,message);
            webSocketServerConcurrentHashMap.get(userId).sendMessage(message);
        }

    }

    /**
     * @Description: 获取在线人数
     * @Param: []
     * @return: java.lang.Integer
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    public static Integer getOnlineCount() {
        return onlineCount;
    }


    /**
     * @Description: 断开连接时的回调函数
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    @OnClose
    public void onClose(){
        if(webSocketServerConcurrentHashMap.containsKey(userId)){
            //删除该用户
            webSocketServerConcurrentHashMap.remove(userId);
            //在线人数-1
            subOnlineCount();
        }
        logger.info("用户退出:{},当前在线人数为{}",userId,WebSocketServer.onlineCount);
    }

    /**
     * @Description: 在线人数+1
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }
    /**
     * @Description: 在线人数-1
     * @Param: []
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/2/8
     */
    public static synchronized void subOnlineCount(){
        WebSocketServer.onlineCount--;
    }
}
