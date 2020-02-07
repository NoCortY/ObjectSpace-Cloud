package cn.objectspace.zuul.filter;

import cn.objectspace.zuul.constant.ConstantPool;
import cn.objectspace.zuul.pojo.ResponseMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
* @Description: zuul pre过滤器
* @Author: NoCortY
* @Date: 2019/12/23
*/
@Component
public class TokenFilter extends ZuulFilter {
    @Autowired RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(TokenFilter.class);
    @Override
    public String filterType() {
        //指定过滤器类型
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //指定其在过滤器链上所处的顺序
        return PRE_DECORATION_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        //是否启用
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //携带cookie
        HttpHeaders headers = new HttpHeaders();
        List<String> cookieList = new ArrayList<>();
        //解决乱码，返回json
        requestContext.getResponse().setContentType("application/json;charset=UTF-8");
        //获取request对象
        HttpServletRequest request = requestContext.getRequest();
        String requestUrl = request.getRequestURI();
        ObjectMapper objectMapper = new ObjectMapper();
        //如果是请求登录和注册,和swagger，则直接放行
        logger.info("Zuul网关拦截:当前访问路径:{}",requestUrl);
        if(requestUrl!=null&&(requestUrl.contains("/ObjectCloud/AuthCenter/AC/login")
                ||requestUrl.contains("/ObjectCloud/AuthCenter/AC/register")
                ||requestUrl.contains("/ObjectCloud/AuthCenter/captcha/kaptcha.jpg")
                ||requestUrl.contains("/ObjectCloud/AuthCenter/UC/mailVerify")
                ||requestUrl.contains("/swagger-ui.html")
                ||requestUrl.contains("/webjars"))
                ||requestUrl.contains("/swagger-resources")
                ||requestUrl.contains("/v2")
                ||requestUrl.contains("/csrf")){
            //放行白名单
            requestContext.setSendZuulResponse(true);
            return null;
        }
        Cookie[] cookies = request.getCookies();
        String token = request.getParameter(ConstantPool.AC_TOKEN);
        //接收AC返回值
        ResponseEntity<ResponseMap> responseMap = null;
        //zuul返回给页面
        ResponseMap<String> zuulResponseMap = new ResponseMap<>();
        if(cookies!=null) {
            //获取token
            for (Cookie cookie : cookies) {
                //备用cookie，为了携带header访问
                cookieList.add(cookie.getName()+"="+cookie.getValue());
            }
        }
        if(token==null) {
            //如果没有携带令牌，可能已经登录，但是没有token，这时候应该进行认证
            //进行认证需要携带cookie,否则cookie将会丢失
            headers.put(HttpHeaders.COOKIE,cookieList);
            logger.info("当前用户没有携带令牌，开始进行认证...");
            HttpEntity httpEntity = new HttpEntity(headers);
            //restTemplate.getForObject("http://ObjectService-LC/LC/abc",ResponseMap.class);
            responseMap = restTemplate.postForEntity(ConstantPool.AC_APPLICATION_NAME + "/AC/authentication", httpEntity, ResponseMap.class);
            //但如果这个用户没有登录，还请求了认证接口，那么认证接口返回会认证失败
            token = (String) responseMap.getBody().getData();
        }
        if(token!=null||(responseMap.getBody()!=null&&ConstantPool.AC_SUCCESS_CODE.equals(responseMap.getBody().getCode()))){
            //如果携带了token，或者经过认证之后获得了token，那么就直接放行，但是此时不排除token是伪造的
            //所以放行之后在访问微服务时，会将这个token再次提交给AC，AC会在进行授权时验证这个token是否是真实的，进而进行pass or reject
            Map<String,List<String>> requestQueryParams = requestContext.getRequestQueryParams();
            //如果参数列表为null，那么就new一个，用于存放token
            if (requestQueryParams==null) requestQueryParams=new HashMap<>();
            ArrayList<String> paramsList = new ArrayList<>();
            paramsList.add(token);
            requestQueryParams.put(ConstantPool.AC_TOKEN, paramsList);
            requestContext.setRequestQueryParams(requestQueryParams);
            requestContext.setSendZuulResponse(true);
            logger.info("认证成功，放行请求");
            return null;
        }else{
            //如果认证之后还不能获得token，说明认证中心驳回了认证的请求，那么一定是没有登录。
            logger.info("认证失败，当前用户没有登录");
            zuulResponseMap.setCode("-1001");
            zuulResponseMap.setMessage("请先进行登录");
            zuulResponseMap.setData("null");
            //驳回
            requestContext.setSendZuulResponse(false);
            try {
                requestContext.setResponseBody(objectMapper.writeValueAsString(zuulResponseMap));
            } catch (JsonProcessingException e) {
                logger.error("转换json失败");
            }
            return null;
        }
    }
}
