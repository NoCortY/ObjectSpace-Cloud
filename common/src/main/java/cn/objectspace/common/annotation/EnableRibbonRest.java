package cn.objectspace.common.annotation;

import cn.objectspace.common.autoconfiguration.RestConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
* @Description: Ribbon负载均衡+负载均衡RestTemplate的开关
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RestConfig.class)
public @interface EnableRibbonRest {
}
