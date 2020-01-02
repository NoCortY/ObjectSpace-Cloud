package cn.objectspace.common.annotation;

import cn.objectspace.common.autoconfiguration.RedisConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
* @Description: RedisUtil开关
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisConfig.class)
public @interface EnableRedisUtil {
}
