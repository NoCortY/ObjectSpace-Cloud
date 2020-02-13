package cn.objectspace.common.cache;

import cn.objectspace.common.util.RedisUtil;
import cn.objectspace.common.util.SerializeUtil;
import cn.objectspace.common.util.SpringUtil;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
* @Description: MyBatis的二级缓存
* @Author: NoCortY
* @Date: 2019/12/20
*/
public class MyBatisCache implements Cache {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Logger logger = LoggerFactory.getLogger(RedisCache.class);
    private String id;
    private RedisUtil redisUtil;

    public MyBatisCache() {}
    public MyBatisCache(String id) {
        logger.info("MyBatis开启二级缓存!");
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    private synchronized RedisUtil getRedisUtil() {
        if(redisUtil==null) {
            redisUtil = SpringUtil.getSpringUtil().getBean(RedisUtil.class);
        }
        return redisUtil;
    }

    /***************做为MyBatis的二级缓存***************/
    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }


    /**
     *
     * 写入缓存
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        RedisUtil redisUtil = getRedisUtil();
        logger.info("将数据回写Redis缓存。");
        //记录当前id下所存的key
        redisUtil.lpush(SerializeUtil.serialize("MyBatisCache:"+id),SerializeUtil.serialize(key));
        //写入缓存
        redisUtil.set(SerializeUtil.serialize(key), SerializeUtil.serialize(value));

    }

    @Override
    public Object getObject(Object key) {
        Object object = null;
        RedisUtil redisUtil = getRedisUtil();
        logger.info("尝试从Redis缓存中get所需数据...");
        byte[] serializedInfo = redisUtil.get(SerializeUtil.serialize(key));
        if(serializedInfo!=null) {
            logger.info("成功命中Redis缓存并获取所需的数据!");
            object = SerializeUtil.unSerialize(serializedInfo);
        }else {
            logger.info("Redis缓存中不存在所需的数据，将进行穿透查询!");
        }
        return object;
    }

    @Override
    public Object removeObject(Object key) {
        RedisUtil redisUtil = getRedisUtil();
        Object object = redisUtil.del(SerializeUtil.serialize(key).toString());
        return object;
    }

    @Override
    public void clear() {
        //导致写入数据库时 所有的数据被清空
        /*RedisUtil redisUtil = getRedisUtil();
        redisUtil.flushAll();*/
        //采用以下方案即可。
        RedisUtil redisUtil = getRedisUtil();
        //如果不存在这个list，那么也不用再删除了，直接返回。
        if(redisUtil.lLen(SerializeUtil.serialize("MyBatisCache:"+id))==null) return;
        List<byte[]> keys = redisUtil.lrange(SerializeUtil.serialize("MyBatisCache:"+id),0,redisUtil.lLen(SerializeUtil.serialize("MyBatisCache:"+id)));
        //如果list中key为null 直接返回
        if(keys==null) return;
        for(byte[] key:keys) {
            redisUtil.del(key);
            //删除redis中该元素
            redisUtil.lpop(SerializeUtil.serialize("MyBatisCache:"+id));
        }
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        // TODO Auto-generated method stub
        return readWriteLock;
    }
}
