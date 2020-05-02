package cn.objectspace.common.autoconfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
public class RequestContextHolderConfig {
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
