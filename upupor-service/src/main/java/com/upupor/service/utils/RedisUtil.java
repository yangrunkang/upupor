package com.upupor.service.utils;

import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 21:34
 */
public class RedisUtil {

    /**
     * 缓存指定时间
     *
     * @param key
     * @param value
     * @param timeout 秒
     */
    public static void set(String key, String value, Long timeout) {
        ValueOperations<String, String> ops = RedisSingleton.getRedisSingleton().getRedisTemplate().opsForValue();
        ops.set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 永久缓存
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        ValueOperations<String, String> ops = RedisSingleton.getRedisSingleton().getRedisTemplate().opsForValue();
        ops.set(key, value);
    }

    public static String get(String key) {
        ValueOperations<String, String> ops = RedisSingleton.getRedisSingleton().getRedisTemplate().opsForValue();
        return ops.get(key);
    }

    public static Boolean remove(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCode.REDIS_KEY_NOT_EXISTS);
        }
        return RedisSingleton.getRedisSingleton().getRedisTemplate().delete(key);
    }

    public static Boolean exists(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCode.REDIS_KEY_NOT_EXISTS);
        }

        Long aLong = RedisSingleton.getRedisSingleton().getRedisTemplate().countExistingKeys(Collections.singleton(key));
        if (Objects.isNull(aLong)) {
            return Boolean.FALSE;
        }

        return aLong > 0;
    }

    public static class RedisSingleton {
        private volatile static RedisSingleton redisSingleton;  //1:volatile修饰

        private final StringRedisTemplate redisTemplate;

        private RedisSingleton() {
            redisTemplate = SpringContextUtils.getBean(StringRedisTemplate.class);
        }

        public static RedisSingleton getRedisSingleton() {
            if (redisSingleton == null) {  //2:减少不要同步，优化性能
                synchronized (RedisSingleton.class) {  // 3：同步，线程安全
                    if (redisSingleton == null) {
                        redisSingleton = new RedisSingleton();  //4：创建singleton 对象
                    }
                }
            }
            return redisSingleton;
        }

        public StringRedisTemplate getRedisTemplate() {
            if (Objects.isNull(redisTemplate)) {
                getRedisSingleton();
            }
            return redisTemplate;
        }
    }


}
