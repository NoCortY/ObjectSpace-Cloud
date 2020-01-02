package cn.objectspace.zuul.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description:
 * 解决由于Bean加载先后顺序造成的MyBatis二级缓存无法注入的工具类
 * 出错类：RedisCache
 * 出错原因：由于Cache在容器中是最先加载（作为二级缓存），所以在RedisUtil未被加载就被注入，导致注入为null
 * 解决方案：使用当前类，做一个懒加载，在使用到RedisUtil时再getBean，这样可以获取到已经被实例化的bean
 * @Author: NoCortY
 * @Date: 2019/10/4
 */
public class SpringUtil implements ApplicationContextAware {

    private  ApplicationContext applicationContext;

    private static SpringUtil springUtil;
    /**
     * @Description: 单例模式
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    private SpringUtil() {}
    public static synchronized SpringUtil getSpringUtil(){
        if(springUtil==null){
            springUtil = new SpringUtil();
        }
        return springUtil;
    }
    /**
     * @Description: 获取ApplicationContext
     * @Param: []
     * @return: org.springframework.context.ApplicationContext
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public  ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * @Description: 通过beanName获取bean
     * @Param: [beanName]
     * @return: java.lang.Object
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public  Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }

    /**
     * @Description:  通过class获取bean
     * @Param: [clazz]
     * @return: T
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public  <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * @Description:  通过Class和beanName获取指定bean
     * @Param: [beanName, clazz]
     * @return: T
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public  <T> T getBean(String beanName, Class<T> clazz) {
        return getApplicationContext().getBean(beanName, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.getSpringUtil().applicationContext == null) {
            SpringUtil.getSpringUtil().applicationContext = applicationContext;
        }
    }

}
