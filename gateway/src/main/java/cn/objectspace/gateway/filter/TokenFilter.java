package cn.objectspace.gateway.filter;

import cn.objectspace.gateway.constant.ConstantPool;
import cn.objectspace.gateway.pojo.ResponseMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class TokenFilter implements GlobalFilter, Ordered {
    @Autowired
    RestTemplate restTemplate;
    private static Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //携带cookie
        HttpHeaders headers = new HttpHeaders();
        List<String> cookieList = new ArrayList<>();
        //解决乱码，返回json
        //获取request对象
        ServerHttpRequest request = exchange.getRequest();
        String requestUrl = request.getPath().toString();
        ObjectMapper objectMapper = new ObjectMapper();
        //如果是请求登录和注册,和swagger，则直接放行
        logger.info("SpringCloud Gateway网关拦截:当前访问路径:{}", requestUrl);
        if (requestUrl != null && (requestUrl.contains("/AC/login")
                || requestUrl.contains("/AC/register")
                || requestUrl.contains("/captcha/kaptcha.jpg")
                || requestUrl.contains("/UC/mailVerify")
                || requestUrl.contains("/swagger-ui.html")
                || requestUrl.contains("/webjars"))
                || requestUrl.contains("/swagger-resources")
                || requestUrl.contains("/v2")
                || requestUrl.contains("/csrf")
                || requestUrl.contains("/CC/server/ping")) {
            //放行白名单
            return chain.filter(exchange);
        }
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        String token = request.getQueryParams().getFirst(ConstantPool.AC_TOKEN);
        //接受AC返回值
        ResponseEntity<ResponseMap> responseMap = null;

        for (String key : cookies.keySet()) {
            //备用cookie，为了携带header访问
            cookieList.add(cookies.getFirst(key).getName() + "=" + cookies.getFirst(key).getValue());
        }

        if (token == null) {
            //如果没有携带令牌，可能已经登录，但是没有token，这时候应该进行认证
            //进行认证需要携带cookie,否则cookie将会丢失
            headers.put(HttpHeaders.COOKIE, cookieList);
            logger.info("当前用户没有携带令牌，开始进行认证...");
            HttpEntity httpEntity = new HttpEntity(headers);
            responseMap = restTemplate.postForEntity(ConstantPool.AC_APPLICATION_NAME + "/AC/authentication", httpEntity, ResponseMap.class);
            //但如果这个用户没有登录，还请求了认证接口，那么认证接口返回会认证失败
            token = (String) responseMap.getBody().getData();
        }
        if (token != null || (responseMap.getBody() != null && ConstantPool.AC_SUCCESS_CODE.equals(responseMap.getBody().getCode()))) {
            //如果携带了token，或者经过认证之后获得了token，那么就直接放行，但是此时不排除token是伪造的
            //所以放行之后在访问微服务时，会将这个token再次提交给AC，AC会在进行授权时验证这个token是否是真实的，进而进行pass or reject

            /*************gateway添加请求参数的方式，很low，但是有用****************/
            URI uri = request.getURI();
            StringBuilder query = new StringBuilder();
            String originalQuery = uri.getRawQuery();
            if (StringUtils.hasText(originalQuery)) {
                query.append(originalQuery);
                if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                    query.append('&');
                }
            }
            query.append(ConstantPool.AC_TOKEN);
            query.append("=");
            query.append(token);
            URI newUri = UriComponentsBuilder.fromUri(uri)
                    .replaceQuery(query.toString())
                    .build(true)
                    .toUri();
            request = exchange.getRequest().mutate().uri(newUri).build();
            /*******************************************************************/
            //queryParams.put(ConstantPool.AC_TOKEN,paramsList);
            logger.info("认证成功，放行请求");
            return chain.filter(exchange.mutate().request(request).build());
        } else {
            //如果认证之后还不能获得token，说明认证中心驳回了认证的请求，那么一定是没有登录。
            logger.info("认证失败，当前用户没有登录");
            //驳回
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
