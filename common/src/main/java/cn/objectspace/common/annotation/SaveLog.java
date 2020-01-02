package cn.objectspace.common.annotation;

import java.lang.annotation.*;

/**
 * @Description:注解：自动保存日志，在Controller方法上加上该注解， 可检测日志信息并持久化到日志中心
 * @Param:
 * @return:
 * @Author: NoCortY
 * @Date: 2019/12/19
 */
@Documented
@Target({ElementType.METHOD})//表示该注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface SaveLog {
    /**
     *当前应用的ID，常量池中有，必填
     * */
    public int applicationId();
}