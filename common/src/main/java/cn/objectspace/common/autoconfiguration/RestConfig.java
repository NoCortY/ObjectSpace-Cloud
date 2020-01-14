package cn.objectspace.common.autoconfiguration;

import cn.objectspace.common.util.RestUtil;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
* @Description: RestTemplate配置类
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Configuration
public class RestConfig {

    /**
     * @Description: 配置带有负载均衡的RestTemplate
     * @Param: []
     * @return: org.springframework.web.client.RestTemplate
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    @Bean
    public RestUtil restUtil(RestTemplate restTemplate){
        RestUtil restUtil = new RestUtil();
        restUtil.setRestTemplate(restTemplate);
        return restUtil;
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    
    /**
     * @Description: 配置负载均衡规则为重试
     * @Param: []
     * @return: com.netflix.loadbalancer.IRule
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    @Bean
    public IRule iRule(){
        return new RoundRobinRule();
    }
}
