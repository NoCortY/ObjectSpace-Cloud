package cn.objectspace.componentcenter;

import cn.objectspace.common.annotation.EnableRedisUtil;
import cn.objectspace.common.annotation.EnableSaveLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
* @Description: 组件中心启动类
* @Author: NoCortY
* @Date: 2019/12/30
*/
@EnableRedisUtil
@EnableSaveLog
@EnableHystrixDashboard
@EnableEurekaClient
@SpringBootApplication
public class ComponentCenterStarter {
    public static void main(String[] args){
        SpringApplication.run(ComponentCenterStarter.class,args);
    }
}
