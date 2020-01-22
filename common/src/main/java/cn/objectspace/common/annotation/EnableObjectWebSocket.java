package cn.objectspace.common.annotation;

import cn.objectspace.common.autoconfiguration.websocket.WebSocketConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WebSocketConfig.class)
public @interface EnableObjectWebSocket {
}
