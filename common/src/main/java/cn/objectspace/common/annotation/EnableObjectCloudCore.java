package cn.objectspace.common.annotation;

import cn.objectspace.common.aop.AutoSaveLogAop;
import cn.objectspace.common.autoconfiguration.RedisConfig;
import cn.objectspace.common.autoconfiguration.RestConfig;
import cn.objectspace.common.autoconfiguration.SpringUtilConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
* @Description: 适合需要本包下的所有bean的应用，全量导入
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RedisConfig.class,
        RestConfig.class,
        SpringUtilConfig.class,
        AutoSaveLogAop.class})
public @interface EnableObjectCloudCore {
}
