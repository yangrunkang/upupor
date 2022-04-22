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

/**
 * 抽象限制器
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-22 00:41
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractLimiter {

    /**
     * 用户Id
     */
    private String userId;
    private LimitType limitType;

    /**
     * 标识key
     *
     * @return
     */
    public String tagKey() {
        return userId + limitType;
    }

    /**
     * 初始化限制器
     */
    public void initLimiter(String userId, LimitType limitType) {
        this.userId = userId;
        this.limitType = limitType;
    }

    private DefaultLimiterConfig limiterConfig() {
        for (DefaultLimiterConfig defaultLimiterConfig : DefaultLimiterConfig.defaultLimiterConfigList()) {
            if (defaultLimiterConfig.getLimitType().equals(limitType)) {
                return defaultLimiterConfig;
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
        Boolean exists = RedisUtil.exists(tagKey());
        if (!exists) {
            return Boolean.FALSE;
        }

        String s = RedisUtil.get(tagKey());
        if (limiterConfig().getFrequency().toString().equals(s)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 使用资源
     */
    private void useSource() {
        RedisUtil.incr(tagKey());
    }

    /**
     * 业务逻辑
     * 1. 初始化,用户首次活动就初始化,后续用户活动使用该限制器
     * 2. 是否达到限制
     */

    public void limit() {
        if (isReachTop()) {
            return;
        }
        useSource();
    }


}
