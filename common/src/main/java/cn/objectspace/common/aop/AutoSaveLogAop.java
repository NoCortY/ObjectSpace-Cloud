package cn.objectspace.common.aop;

import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.Log;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.util.RestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


/**
 * 
* @Description: 自动打印日志并将日志入库
* @Author: NoCortY
* @Date: 2019年11月22日
 */
@Aspect
@Component
public class AutoSaveLogAop {
    private Log log;
    private Logger logger = LoggerFactory.getLogger(AutoSaveLogAop.class);
    @Autowired
    private RestUtil restUtil;
    /**
     * 拦截所有SaveLog注解的方法
     */
    @Pointcut("@annotation(cn.objectspace.common.annotation.SaveLog)")
    public void autoLogCutPoint(){}
    /**
     * @Description: 环绕通知
     * @Param: args
     * @return: Object
     * @Author: NoCortY
     * @Date: 2019年11月22日
     */
    @Around("autoLogCutPoint()")
    public Object around(ProceedingJoinPoint joinPoint){
        SaveLog saveLogAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(SaveLog.class);
        //获取应用ID
        Integer applicationId = saveLogAnnotation.applicationId();
        ObjectMapper objectMapper = new ObjectMapper();
        //执行切入点
        //返回参数
        Object object = null;
        try {
            object = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.info("====================================访问日志===================================");
            logger.error("                      执行切入点出错！请检查{}的业务逻辑!",joinPoint.getSignature().getName());
            logger.error("                      错误原因:{}",throwable.getMessage());
            logger.info("=============================================================================");
            return null;
        }
        //获取当前日期
        Date operateDate = new Date();
        //获取Request
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes)ra;
        HttpServletRequest request = sra.getRequest();
        //获取请求路径(操作的接口)
        String operateInterface = request.getRequestURI();
        //获取入参
        StringBuffer inputParameter = new StringBuffer();
        Object[] objects = joinPoint.getArgs();
        for(Object o : objects){
            if(!(o instanceof HttpServletRequest)&&!(o instanceof HttpServletResponse)){
                //过滤HttpServletRequest和HttpServletResponse
                //且密码不能显示
                try {
                    //将入参转换为json格式
                    String in = objectMapper.writeValueAsString(o);
                    if(in.contains("userPassword")){
                        in = "当前日志中含有敏感信息，不予显示";
                    }
                    inputParameter.append(in);
                } catch (JsonProcessingException e) {
                    inputParameter.append("获取入参异常");
                    logger.error("获取入参异常");
                    logger.error("异常信息:{}",e.getMessage());
                }
                inputParameter.append("|");
            }
        }
        //IP地址
        String operateUserIp = getRemoteHost(request);
        String ip = request.getRemoteAddr();
        //获取出参
        String outputParameter = null;
        try {
        	outputParameter= objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        	outputParameter = "获取出参异常";
            logger.error("获取出参异常");
            logger.error("异常信息{}",e.getMessage());
        }

        logger.info("====================================访问日志===================================");
        logger.info("                      访问时间:{}",operateDate);
        logger.info("                      访问接口:{}",operateInterface);
        logger.info("                      入参:{}",inputParameter);
        logger.info("                      出参:{}",outputParameter);
        logger.info("                      访问者IP(A):{}",operateUserIp);
        logger.info("                      访问者IP(B):{}",ip);
        logger.info("=======================================================================");
        log = new Log(operateDate,inputParameter.toString(),outputParameter,operateInterface,operateUserIp,applicationId);
        //调用日志中心，发送日志入库请求
        restUtil.postObjectByAppName(ConstantPool.LogCenter.LC_APPLICATION_NAME+"/LC/recordLog",log,ResponseMap.class);
        
        return object;
    }

    /**
     * @Description: 获取目标主机ip
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/11/15
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}

