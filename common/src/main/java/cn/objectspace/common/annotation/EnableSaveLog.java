package cn.objectspace.common.annotation;

import cn.objectspace.common.aop.AutoSaveLogAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
* @Description: 接入日志中心开关
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Documented
@Target({ElementType.TYPE})//表示该注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)
@Import({AutoSaveLogAop.class})
public @interface EnableSaveLog {
}

