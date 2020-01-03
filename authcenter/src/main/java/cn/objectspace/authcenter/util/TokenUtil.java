package cn.objectspace.authcenter.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
* @Description: 令牌生成器
* @Author: NoCortY
* @Date: 2019/12/16
*/
public class TokenUtil {
    private Logger logger = LoggerFactory.getLogger(TokenUtil.class);
    private TokenUtil() {}
    private static TokenUtil instance;
    /**
     * @Description: 单例模式
     * @Param: []
     * @return: cn.objectspace.authcenter.util.TokenUtil
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public synchronized static TokenUtil getInstance() {
        if(instance==null) {
            instance = new TokenUtil();
        }
        return instance;
    }
    /**
     * @Description: 生成令牌
     * @Param: []
     * @return: java.lang.String
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public String generateTokeCode() {
        String value = System.currentTimeMillis() + new Random().nextInt() + "";
        // 获取数据指纹，指纹是唯一的
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("生成token异常");
            logger.error("异常信息:"+e.getMessage());
        }
        byte[] b = md.digest(value.getBytes());// 产生数据的指纹
        // Base64编码
        String to =  Base64.encodeBase64URLSafeString(b);
        return to;// 制定一个编码
    }
}
