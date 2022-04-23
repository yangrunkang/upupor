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

package com.upupor.web.aop;


import com.upupor.limiter.AbstractLimiter;
import com.upupor.limiter.UpuporLimit;
import com.upupor.service.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.upupor.web.aop.OrderConstant.LIMITER_ORDER;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-23 01:32
 * @email: yangrunkang53@gmail.com
 */
@Aspect
@Component
@Order(LIMITER_ORDER)
public class LimiterAspectAdvice extends AbstractLimiter {


    @Pointcut("@annotation(com.upupor.limiter.UpuporLimit)")
    public void upuporLimiterAspect() {
    }

    @Before("upuporLimiterAspect() && @annotation(annotation)")
    public void doBefore(JoinPoint joinPoint, UpuporLimit annotation) {

        String userId = ServletUtils.getUserId();
        if(StringUtils.isEmpty(userId)){
            return;
        }

        // 初始化限制器
        initLimiter(userId,annotation.limitType());
        // 执行限流操作
        limit();
    }




}
