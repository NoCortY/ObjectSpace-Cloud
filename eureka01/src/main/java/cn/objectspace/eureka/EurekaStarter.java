package cn.objectspace.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
* @Description: 注册中心
* @Author: NoCortY
* @Date: 2019/12/19
*/
@EnableEurekaServer
@SpringBootApplication
public class EurekaStarter {
    /**
     * @Description: 注册中心启动
     * @Param: [args]
     * @return: void
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public static void main(String[] args){
        SpringApplication.run(EurekaStarter.class,args);
    }
}
