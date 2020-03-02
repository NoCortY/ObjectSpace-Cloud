package cn.objectspace.componentcenter.filter;

import cn.objectspace.common.constant.ConstantPool;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {
    /**
     * @Description: Handler处理前调用
     * @Param: [serverHttpRequest, serverHttpResponse, webSocketHandler, map]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/3/1
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            // 获取session中的用户id，放到websocket session中。
            Integer userId = (Integer) request.getServletRequest().getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
            map.put(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY, userId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
