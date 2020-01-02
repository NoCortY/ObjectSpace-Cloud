package cn.objectspace.authcenter;

import cn.objectspace.common.annotation.EnableObjectCloudCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
* @Description: 认证中心启动类
* @Author: NoCortY
* @Date: 2019/12/19
*/
@EnableObjectCloudCore
@EnableEurekaClient
@EnableHystrixDashboard
@SpringBootApplication
public class AuthCenterStarter {
    /**
     * @Description: 认证中心启动方法
     * @Param: [args]
     * @return: void
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthCenterStarter.class,args);
    }
}
