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

package com.upupor.web.aop.checker.controller;

import com.upupor.limiter.AbstractLimiter;
import com.upupor.limiter.UpuporLimit;
import com.upupor.service.utils.ServletUtils;
import com.upupor.web.aop.checker.controller.dto.ControllerCheckerDto;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.upupor.service.utils.ServletUtils.checkIsLogin;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-23 15:20
 * @email: yangrunkang53@gmail.com
 */
@Service
@Order(0)
public class LimiterChecker extends AbstractLimiter implements ControllerAspectChecker {
    @Override
    public void check(ControllerCheckerDto controllerCheckerDto) {


        MethodSignature signature = (MethodSignature) controllerCheckerDto.getProceedingJoinPoint().getSignature();
        UpuporLimit annotation = signature.getMethod().getAnnotation(UpuporLimit.class);
        if(Objects.isNull(annotation)){
            return;
        }

        // 如果需要登录,就使用用户id,否则使用sessionId
        String businessId;
        if(annotation.needLogin()){
            checkIsLogin();
            businessId = ServletUtils.getUserId();
        }else {
            businessId=controllerCheckerDto.getRequest().getSession().getId();
        }

        // 初始化限制器
        initInterfaceLimiter(businessId, annotation.limitType());

        // 执行限流操作
        limit();
    }


}