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

package com.upupor.limiter.current_limiting;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.upupor.limiter.current_limiting.LimiterConstant.INTERVAL_10S;
import static com.upupor.limiter.current_limiting.LimiterConstant.INTERVAL_30S;

/**
 * 限制器
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-22 00:46
 * @email: yangrunkang53@gmail.com
 */
@Builder
@Getter
public class Limiter {

    /**
     * 限制类型
     */
    private LimitType limitType;

    /**
     * 请求频率
     */
    private Integer frequency;

    /**
     * 在xx秒内
     *
     * @note: 在 withinSeconds 秒内只允许请求 frequency 次
     */
    private Integer withinSeconds;

    /**
     * 页面Url限制,如果有则限制
     */
    private String pageUrl;

    /**
     * 默认的系统资源配置
     *
     * @return
     */
    public static List<Limiter> interfaceLimiterList() {
        List<Limiter> limiterList = new ArrayList<>();
        limiterList.add(Limiter.builder().limitType(LimitType.CREATE_CONTENT).frequency(2).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CREATE_RADIO).frequency(2).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CREATE_MESSAGE_ON_BOARD).frequency(2).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.SEND_EMAIL_VERIFY_CODE).frequency(2).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.UPLOAD_RADIO_FILE).frequency(2).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.FEED_BACK).frequency(2).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.UPLOAD_CONTENT_IMAGE).frequency(4).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.UPLOAD_PROFILE_IMAGE).frequency(3).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CREATE_TODO).frequency(5).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CREATE_COMMENT).frequency(3).withinSeconds(INTERVAL_30S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CLICK_COLLECT).frequency(2).withinSeconds(INTERVAL_10S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CLICK_LIKE).frequency(2).withinSeconds(INTERVAL_10S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.CLICK_ATTENTION).frequency(2).withinSeconds(INTERVAL_10S).build());
        limiterList.add(Limiter.builder().limitType(LimitType.LOGIN).frequency(5).withinSeconds(INTERVAL_30S).build());
        return limiterList;
    }

}
