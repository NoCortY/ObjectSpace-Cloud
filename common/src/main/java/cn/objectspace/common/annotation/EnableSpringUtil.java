package cn.objectspace.common.annotation;

import cn.objectspace.common.autoconfiguration.SpringUtilConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
* @Description: SpringUtil开关
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringUtilConfig.class)
public @interface EnableSpringUtil {
}
