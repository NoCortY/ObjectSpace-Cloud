package cn.objectspace.gateway.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Description: 跨域过滤器
 * @Author: NoCortY
 * @Date: 2020/3/2
 */
@Component
public class CorsWebFilter implements WebFilter {

    private static final String ALL = "*";

    private static final String MAX_AGE = "18000L";

    @Override

    public Mono<Void> filter(ServerWebExchange ctx, WebFilterChain chain) {

        ServerHttpRequest request = ctx.getRequest();

        String path = request.getPath().value();

        ServerHttpResponse response = ctx.getResponse();

        if ("/favicon.ico".equals(path)) {

            response.setStatusCode(HttpStatus.OK);

            return Mono.empty();

        }


        if (!CorsUtils.isCorsRequest(request)) {

            return chain.filter(ctx);

        }

        HttpHeaders requestHeaders = request.getHeaders();


        HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();

        HttpHeaders headers = response.getHeaders();

        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());

        headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());

        if (requestMethod != null) {

            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());

        }

        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);

        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

        if (request.getMethod() == HttpMethod.OPTIONS) {

            response.setStatusCode(HttpStatus.OK);

            return Mono.empty();

        }


        return chain.filter(ctx);

    }


}