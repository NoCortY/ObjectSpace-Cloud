package cn.objectspace.logcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.scheduling.annotation.EnableAsync;

/**
* @Description: 日志中心启动器
* @Author: NoCortY
* @Date: 2019/12/19
*/
@EnableEurekaClient
@EnableHystrixDashboard
@EnableAsync
@SpringBootApplication
public class LogCenterStarter {
    /**
     * @Description: 日志中心启动方法
     * @Param: [args]
     * @return: void
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public static void main(String[] args) {
        SpringApplication.run(LogCenterStarter.class,args);
    }
}
