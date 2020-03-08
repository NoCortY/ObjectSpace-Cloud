package cn.objectspace.componentcenter.config;


import cn.objectspace.componentcenter.controller.websocket.ServerResumeWebSocketHandler;
import cn.objectspace.componentcenter.controller.websocket.WebSSHWebSocketHandler;
import cn.objectspace.componentcenter.filter.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
* @Description: 开启WebSocket支持
* @Author: NoCortY
* @Date: 2020/2/8
*/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /*@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }*/
    @Autowired
    ServerResumeWebSocketHandler serverResumeWebSocketHandler;
    @Autowired
    WebSSHWebSocketHandler webSSHWebSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道
        //指定处理器和路径
        webSocketHandlerRegistry.addHandler(serverResumeWebSocketHandler, "/serverResumeWS")
                //自定义拦截器
                .addInterceptors(new WebSocketInterceptor())
                //允许跨域
                .setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(webSSHWebSocketHandler, "webSSHWS")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
        // sockJs通道
        webSocketHandlerRegistry.addHandler(serverResumeWebSocketHandler, "/serverGeneralSJ")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*")
                // 开启sockJs支持
                .withSockJS();
    }
}
