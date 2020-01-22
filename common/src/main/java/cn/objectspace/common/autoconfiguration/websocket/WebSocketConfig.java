package cn.objectspace.common.autoconfiguration.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //websocket通道
        //指定处理器和路径
        webSocketHandlerRegistry.addHandler(new WebSocketHandler() ,"/websocket")
                                .addInterceptors(new WebSocketInterceptor()).setAllowedOrigins("*");
        //sockJs通道
        webSocketHandlerRegistry.addHandler(new WebSocketHandler(), "/sock-js")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*")
                // 开启sockJs支持
                .withSockJS();
    }
}
