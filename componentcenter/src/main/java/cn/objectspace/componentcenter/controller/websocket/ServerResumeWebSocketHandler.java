package cn.objectspace.componentcenter.controller.websocket;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.componentcenter.pojo.dto.ServerResumeDto;
import cn.objectspace.componentcenter.service.ServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
//@Scope("prototype")
public class ServerResumeWebSocketHandler implements WebSocketHandler {

    @Autowired
    private ServerService serverService;

    private static Logger logger = LoggerFactory.getLogger(ServerResumeWebSocketHandler.class);
    //用户session缓存
    private static Map<String, Object> sessionMap = new ConcurrentHashMap<>();

    public static Set<String> sessionMapKeySet() {
        if (!sessionMap.isEmpty())
            return sessionMap.keySet();
        else
            return null;
    }

    public static int sessionMapSize() {
        return sessionMap.size();
    }

    public static Object sessionMapGet(String key) {
        return sessionMap.get(key);
    }

    public static boolean sessionMapIsEmpty() {
        return sessionMap.isEmpty();
    }
    /**
     * @Description: WebSocket连接创建后调用
     * @Param: [webSocketSession]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/1
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        Integer userId = (Integer) webSocketSession.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        sessionMap.put(String.valueOf(userId), webSocketSession);
        logger.info("用户:{},与服务器管理中心建立websocket连接", userId);
        logger.info("开始获取服务器概要信息...");
        List<ServerResumeDto> serverResumes = serverService.getServerResumes(userId);
        ObjectMapper objectMapper = new ObjectMapper();
        String serverResumeJson = objectMapper.writeValueAsString(serverResumes);
        senMessage(userId, serverResumeJson);
    }

    /**
     * @Description: 接收到消息时调用
     * @Param: [webSocketSession, webSocketMessage]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/1
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

        if (webSocketMessage instanceof TextMessage) {
            logger.info("用户:{},发送消息:{}", webSocketSession.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY), webSocketMessage.toString());
        } else if (webSocketMessage instanceof BinaryMessage) {

        } else if (webSocketMessage instanceof PongMessage) {

        } else {
            System.out.println("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    /**
     * @Description: 连接出错时调用
     * @Param: [webSocketSession, throwable]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/1
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        sessionMap.remove(webSocketSession.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
        logger.info("");
    }

    /**
     * @Description: 连接关闭时调用
     * @Param: [webSocketSession, closeStatus]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/1
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        Integer userId = (Integer) webSocketSession.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        sessionMap.remove(String.valueOf(userId));
        logger.info("用户:{},断开连接", userId);

    }


    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * @Description: 后台给前端推送信息
     * @Param: [userId, message]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/1
     */
    public static void senMessage(Integer userId, String message) throws IOException {
        WebSocketSession session = (WebSocketSession) sessionMap.get(String.valueOf(userId));
        session.sendMessage(new TextMessage(message));
    }
}
