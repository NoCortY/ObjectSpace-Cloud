package cn.objectspace.common.autoconfiguration;

import cn.objectspace.common.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @Description: 自定义Spring获取bean工具类的配置类
* @Author: NoCortY
* @Date: 2019/12/16
*/
@Configuration
public class SpringUtilConfig {
    /**
     * @Description: 配置springUtil Bean
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    @Bean
    public SpringUtil springUtil(){
        SpringUtil springUtil = SpringUtil.getSpringUtil();
        return springUtil;
    }
}
