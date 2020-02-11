package cn.objectspace.componentcenter.filter;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.pojo.entity.URPDto;
import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.RestUtil;
import cn.objectspace.common.util.SerializeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(filterName = "ACFilter",urlPatterns = "/**")
public class ACFilter implements Filter {
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(ACFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //ServlerRequest转HttpServletRequest
        request = (HttpServletRequest) request;
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        //System.out.println(requestURI);
        if("/CC/server/ping".equals(requestURI)){
            //如果是发送心跳的，直接放行即可
            logger.info("访问白名单url");
            chain.doFilter(request,response);
            return;
        }
        if(((HttpServletRequest) request).getSession().getAttribute("CCUserId")!=null){
            logger.info("用户已通过授权，直接放行");
            chain.doFilter(request,response);
        }else{
            logger.info("该用户第一次访问本服务，进行授权...");

            String token = request.getParameter(ConstantPool.Shiro.AC_TOKEN);
            //接收返回值
            ResponseMap responseMap = null;
           /* //携带cookie
            HttpHeaders headers = new HttpHeaders();
            List<String> cookieList = new ArrayList<>();
            //Cookie
            Cookie[] cookies = ((HttpServletRequest) request).getCookies();
            if(cookies!=null){
                for(Cookie cookie:cookies){
                    cookieList.add(cookie.getName()+"="+cookie.getValue());
                }
            }*/
            //headers.put(HttpHeaders.COOKIE,cookieList);
            logger.info("携带token访问授权接口...");
           // HttpEntity httpEntity = new HttpEntity(headers);
            responseMap = restUtil.getRestTemplate().postForObject(ConstantPool.Shiro.AC_APPLICATION_NAME+"/AC/authorization/"+ConstantPool.ComponentCenter.APPLICATION_ID+"/"+token,null,ResponseMap.class);
            //responseMap = restUtil.getRestTemplate().postForEntity(ConstantPool.Shiro.AC_APPLICATION_NAME+"/AC/authorization/"+ConstantPool.ComponentCenter.APPLICATION_ID,httpEntity,ResponseMap.class);
            if(responseMap.getData()!=null){
                ObjectMapper objectMapper = new ObjectMapper();
                System.out.println(objectMapper.writeValueAsString(responseMap.getData()));
                URPDto urpDto =  objectMapper.readValue(objectMapper.writeValueAsString(responseMap.getData()),URPDto.class);
                ((HttpServletRequest) request).getSession().setAttribute("CCUserId",urpDto.getUserId());
                //用户授权信息存入redis，下次不用再访问AC
                redisUtil.set(SerializeUtil.serialize(ConstantPool.ComponentCenter.URPDTO_REDIS_KEY_CC+urpDto.getUserId()),SerializeUtil.serialize(urpDto),3600);
                //放行
                chain.doFilter(request,response);
            }
        }
    }

}
