package cn.objectspace.common.autoconfiguration;

import cn.objectspace.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description: Redis配置类
 * @Author: NoCortY
 * @Date: 2019/10/4
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;

    @Bean(name = "jedisPool")
    public JedisPool redisPoolFactory()  throws Exception{
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 是否启用pool的jmx管理功能, 默认true
        jedisPoolConfig.setJmxEnabled(true);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password,database);
        return jedisPool;
    }


    /**
     * @Description: 自定义redis工具类配置bean
     * @Param: [jedisPool]
     * @return: cn.objectspace.common.util.RedisUtil
     * @Author: NoCortY
     * @Date: 2019/12/16
     */
    @Bean
    public RedisUtil redisUtil(JedisPool jedisPool){
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setJedisPool(jedisPool);
        return redisUtil;
    }
}