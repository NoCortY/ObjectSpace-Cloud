package cn.objectspace.zuul;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
* @Description: API路由网关
* @Author: NoCortY
* @Date: 2019/12/20
*/
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class ZuulStarter {
    /**
     * @Description: API路由网关启动方法
     * @Param: [args]
     * @return: void
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    public static void main(String[] args) {
        SpringApplication.run(ZuulStarter.class,args);
    }
}
