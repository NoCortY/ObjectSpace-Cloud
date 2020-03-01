package cn.objectspace.componentcenter.config;

import cn.objectspace.common.constant.ConstantPool;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CCWebSocketHandler implements WebSocketHandler {
    public static Map<String, Object> sessionMap = new ConcurrentHashMap<>();

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
        sessionMap.remove(webSocketSession.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
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
    public void senMessage(String userId, String message) throws IOException {
        WebSocketSession session = (WebSocketSession) sessionMap.get(userId);
        session.sendMessage(new TextMessage(message));
    }
}
