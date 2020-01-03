package cn.objectspace.authcenter.config;

import cn.objectspace.authcenter.dao.ShiroDao;
import cn.objectspace.authcenter.filter.URLPathMatchingFilter;
import cn.objectspace.authcenter.pojo.entity.UrlFilter;
import cn.objectspace.authcenter.realm.DatabaseRealm;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.util.SpringUtil;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: shiro配置
* @Author: NoCortY
* @Date: 2019/12/16
*/
@Configuration
public class ShiroConfig {
    /**
     * @Description: 记住我
     * @Param:
     * @return: SimpleCookie cookie
     * @Author: NoCortY
     * @Date: 2019/12/12
     */
    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("rememberMe");
        simpleCookie.setMaxAge(20000000);
        return simpleCookie;
    }

    /**
     * @Description: 记住我
     * @Param:
     * @return: CookieRememberMeManager Cookie管理
     * @Author: NoCortY
     * @Date: 2019/12/12
     */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager(SimpleCookie simpleCookie) {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);
        return cookieRememberMeManager;
    }

    /**
     * 相当于调用SecurityUtils.setSecurityManager(securityManager)
     * @param securityManager
     * @return
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(SecurityManager securityManager) {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(securityManager);
        return methodInvokingFactoryBean;
    }
    /**
     * @Description: md5加密
     * @Param: []
     * @return: org.apache.shiro.authc.credential.HashedCredentialsMatcher
     * @Author: NoCortY
     * @Date: 2019/12/12
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName(ConstantPool.Shiro.ALGORITHM_NAME);
        matcher.setHashIterations(ConstantPool.Shiro.ITERATIONS);
        return matcher;
    }

    /**
     * @Description: 退出登录的拦截器
     * @Param: []
     * @return: org.apache.shiro.web.filter.authc.LogoutFilter
     * @Author: NoCortY
     * @Date: 2019/10/4
     */
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter();
        return logoutFilter;
    }
    /**
     * @Description: 自定义url拦截器Bean，只要不存在于FilterChainDefinitionMap中value为anon的url，都需要到这个拦截器进行权限验证
     * @Param: []
     * @return: com.objectspace.coorperation.shiro.URLPathMatchingFilter
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    public URLPathMatchingFilter URLPathMatchingFilter() {
        URLPathMatchingFilter urlPathMatchingFilter = new URLPathMatchingFilter();
        return urlPathMatchingFilter;
    }
    /**
     * @Description: 自定义Realm密码验证与加密
     * @Param: [hashedCredentialsMatcher]
     * @return: cn.objectspace.authcenter.realm.DatabaseRealm
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    @Bean
    public DatabaseRealm databaseRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        DatabaseRealm databaseRealm = new DatabaseRealm();
        databaseRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return databaseRealm;
    }

    /**
     * @Description: SecurityManager环境
     * @Param: [databaseRealm, cookieRememberMeManager]
     * @return: org.apache.shiro.mgt.DefaultSecurityManager
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    @Bean
    public SecurityManager defaultWebSecurityManager(DatabaseRealm databaseRealm, CookieRememberMeManager cookieRememberMeManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(databaseRealm);
        securityManager.setRememberMeManager(cookieRememberMeManager);
        return securityManager;
    }
    /**
     * @Description: 配置过滤器链
     * @Param: [defaultSecurityManager, filterChainDefinitionMap]
     * @return: org.apache.shiro.spring.web.ShiroFilterFactoryBean
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    @Bean(name="shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager defaultWebSecurityManager, LinkedHashMap<String,String> filterChainDefinitionMap){
        //配置shiro的过滤器工厂类
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置过滤器的安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        //配置登录页面地址
        //shiroFilterFactoryBean.setLoginUrl("/views/login");
        //如果访问到未授权url，跳转到403界面
        //shiroFilterFactoryBean.setUnauthorizedUrl("/views/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/**","url");
        Map<String, Filter> filters = new LinkedHashMap<String,Filter>();
        filters.put("url", URLPathMatchingFilter());
        filters.put("logout", logoutFilter());
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }

    /**
     * @Description: 维护生命周期
     * @Param: []
     * @return: org.apache.shiro.spring.LifecycleBeanPostProcessor
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * @Description:从数据库中获取过滤器链
     * @Param: []
     * @return: java.util.LinkedHashMap<java.lang.String,java.lang.String>
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    @Bean
    @DependsOn({"shiroDao","springUtil"})
    public LinkedHashMap<String,String> filterChainDefinitionMap(){
        LinkedHashMap<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        //必须使用SpringUtil 否则空指针
        ShiroDao shiroDao = SpringUtil.getSpringUtil().getBean(ShiroDao.class);
        List<UrlFilter> urlFilterList = shiroDao.queryAllUrlFilter();
        for(UrlFilter urlFilter:urlFilterList){
            filterChainDefinitionMap.put(urlFilter.getUrl(),urlFilter.getFilter());
        }
        return filterChainDefinitionMap;
    }

    /**
     * @Description: 授权生效
     * @Param: []
     * @return: org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
     * @Author: NoCortY
     * @Date: 2019/12/13
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        return new AuthorizationAttributeSourceAdvisor();
    }
}