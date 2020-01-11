package cn.objectspace.authcenter.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Description: 自定义URL过滤器，基于url进行权限控制
 * @Author: NoCortY
 * @Date: 2019/10/4
 */
public class URLPathMatchingFilter extends PathMatchingFilter {
    private Logger logger = LoggerFactory.getLogger(URLPathMatchingFilter.class);
    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        String requestURI = getPathWithinApplication(request);

        logger.info("URL过滤器 请求路径:{}",requestURI);
        Subject subject = SecurityUtils.getSubject();
        // 如果没有登录，就跳转到登录页面
        if (!subject.isAuthenticated())
            return false;

        // 看看这个路径权限里有没有维护，如果没有维护，一律放行(也可以改为一律不放行)
        // 由于Spring无法管理Service，所以代码逻辑改为，如果没有维护，一律不放行
        /*
         * boolean needInterceptor = shiroService.needInterceptor(requestURI); if
         * (!needInterceptor) { return true; } else {}
         */
        if (subject.isPermitted(requestURI)) return true;
        else {
            UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径 " + requestURI + " 的权限");
            subject.getSession().setAttribute("ex", ex);
            return false;
        }
    }
}