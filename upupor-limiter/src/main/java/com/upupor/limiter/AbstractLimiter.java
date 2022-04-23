/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.limiter;

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.concurrent.TimeUnit;

import static com.upupor.framework.CcConstant.DASH;

/**
 * 抽象限制器
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-22 00:41
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
public abstract class AbstractLimiter {
    /**
     * 业务ID
     */
    private String businessId;
    private LimitType limitType;

    private StringRedisTemplate redisTemplate;
    private Limiter limiter;
    /**
     * 标识key
     *
     * @return
     */
    public String tagKey() {
        return limitType + DASH + businessId;
    }

    private void commonInit(){
        // 初始化
        this.redisTemplate = RedisUtil.RedisSingleton.getRedisSingleton().getRedisTemplate();
    }

    /**
     * 初始化接口限制器
     */
    public void initInterfaceLimiter(String businessId, LimitType limitType) {
        commonInit();
        this.businessId = businessId;
        this.limitType = limitType;
        this.limiter = limiterConfig();
    }

    /**
     * 初始化页面限制器
     */
    public void initPageLimiter(String businessId, Limiter limiter) {
        commonInit();

        this.businessId = businessId;
        this.limiter = limiter;
        this.limitType = limiter.getLimitType();

    }

    private Limiter limiterConfig() {
        for (Limiter limiter : Limiter.interfaceLimiterList()) {
            if (limiter.getLimitType().equals(limitType)) {
                return limiter;
            }
        }
        throw new BusinessException(ErrorCode.WITHOUT_DEFAULT_LIMIT_CONFIG);
    }

    /**
     * 是否达到限制
     *
     * @return
     */
    private Boolean isReachTop() {
        String key = tagKey();
        Integer windowInSecond = limiter.getWithinSeconds();
        Integer maxCount = limiter.getFrequency();
        // 当前时间
        long currentMs = System.currentTimeMillis();
        // 窗口开始时间
        long windowStartMs = currentMs - windowInSecond * 1000L;
        // 按score统计key的value中的有效数量
        Long count = redisTemplate.opsForZSet().count(key, windowStartMs, currentMs);
        // 已访问次数 >= 最大可访问值
        return count >= maxCount;

    }


    /**
     * 业务逻辑
     * 1. 初始化,用户首次活动就初始化,后续用户活动使用该限制器
     * 2. 是否达到限制
     */

    public void limit() {
        if (isReachTop()) {
            Long expire = redisTemplate.getExpire(tagKey(), TimeUnit.SECONDS);
            throw new BusinessException(ErrorCode.REQUEST_TOO_MATCH.getCode(),"请求过于频繁,请"+expire+"秒后重试");
        }
        canAccess();
    }


    /**
     * 判断key的value中的有效访问次数是否超过最大限定值maxCount，若没超过，调用increment方法，将窗口内的访问数加一
     * 判断与数量增长同步处理
     */
    public boolean canAccess() {
        String key = tagKey();
        Integer maxCount = limiter.getFrequency();

        log.info("redis key = {}", key);
        //按key统计集合中的有效数量
        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count < maxCount) {
            increment();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 滑动窗口计数增长
     */
    public void increment() {
        String key = tagKey();
        Integer windowInSecond = limiter.getWithinSeconds();
        // 当前时间
        long currentMs = System.currentTimeMillis();
        // 窗口开始时间
        long windowStartMs = currentMs - windowInSecond * 1000;
        // 单例模式(提升性能)
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        // 清除窗口过期成员
        zSetOperations.removeRangeByScore(key, 0, windowStartMs);
        // 添加当前时间 value=当前时间戳 score=当前时间戳
        zSetOperations.add(key, String.valueOf(currentMs), currentMs);
        // 设置key过期时间
        redisTemplate.expire(key, windowInSecond, TimeUnit.SECONDS);
    }

}
