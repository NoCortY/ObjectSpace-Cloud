package cn.objectspace.componentcenter.config;

import cn.objectspace.componentcenter.filter.WebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
* @Description: 开启WebSocket支持
* @Author: NoCortY
* @Date: 2020/2/8
*/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道
        //指定处理器和路径
        webSocketHandlerRegistry.addHandler(new CCWebSocketHandler(), "/componentWS")
                //自定义拦截器
                .addInterceptors(new WebSocketInterceptor())
                //允许跨域
                .setAllowedOrigins("*");
        // sockJs通道
        webSocketHandlerRegistry.addHandler(new CCWebSocketHandler(), "/componentSJ")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*")
                // 开启sockJs支持
                .withSockJS();
    }
}
