package cn.objectspace.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
* @Description: RestTemplate工具类
* @Author: NoCortY
* @Date: 2019/12/19
*/
public class RestUtil {
    private Logger logger = LoggerFactory.getLogger(RestUtil.class);
    private RestTemplate restTemplate;

    public RestTemplate getRestTemplate(){
        return this.restTemplate;
    }
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    /**
     * @Description: 通过url发送post带对象名的请求
     * @Param: [url, name, reqObject, responseType]
     * @return: T
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    public <T> T postJsonObjectWithName(String url, String name, Object reqObject, Class<T> responseType){
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedMultiValueMap<String,Object> multiValueMap = new LinkedMultiValueMap<>();
        String reqJson = null;
        try {
           reqJson = objectMapper.writeValueAsString(reqObject);
        } catch (JsonProcessingException e) {
            logger.error("postJsonObjectWithName转换Json异常");
            logger.error("异常信息:{}",e.getMessage());
            return null;
        }
        multiValueMap.add(name,reqJson);
        return restTemplate.postForObject(url,multiValueMap,responseType);
    }
    /**
     * @Description: 通过微服务名发送带对象名的post
     * @Param: [applicationName, name, reqObject, responseType]
     * @return: T
     * @Author: NoCortY
     * @Date: 2019/12/20
     */
    public <T> T postJsonObjectWithNameByAppName(String applicationName, String name, Object reqObject, Class<T> responseType){
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedMultiValueMap<String,Object> multiValueMap = new LinkedMultiValueMap<>();
        String reqJson = null;
        try {
            reqJson = objectMapper.writeValueAsString(reqObject);
        } catch (JsonProcessingException e) {
            logger.error("postJsonObjectWithName转换Json异常");
            logger.error("异常信息:{}",e.getMessage());
            return null;
        }
        multiValueMap.add(name,reqJson);
        return restTemplate.postForObject(applicationName,multiValueMap,responseType);
    }
    /**
     * @Description: 发送对象（post）
     * @Param: [applicationName, reqObject, responseType]
     * @return: T
     * @Author: NoCortY
     * @Date: 2019/12/23
     */
    public <T> T postObjectByAppName(String applicationName,Object reqObject,Class<T> responseType){
        return restTemplate.postForObject(applicationName,reqObject,responseType);
    }
}
