/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.app.aop;


import com.upupor.api.common.ApiErrorCode;
import com.upupor.api.common.ApiException;
import com.upupor.api.common.ApiResp;
import com.upupor.framework.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * restful 接口切面
 *
 * @author: Yang RunKang(cruise)
 * @created: 2019/12/23 02:29
 */
@Slf4j
@Aspect
@Component
public class ApiAspectAspect {

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.upupor.app.api..*.*(..))")
    public void controller() {
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("controller()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            ApiResp<String> errorResp = new ApiResp<>();
            if (throwable instanceof ApiException) {
                ApiException businessException = (ApiException) throwable;
                errorResp.setCode(businessException.getCode());
                errorResp.setData(businessException.getMessage());
            } else if (throwable instanceof BusinessException) {
                BusinessException businessException = (BusinessException) throwable;
                errorResp.setCode(businessException.getCode());
                errorResp.setData(throwable.getMessage());
            } else {
                errorResp.setCode(ApiErrorCode.API_ERROR.getCode());
                errorResp.setData(throwable.getMessage());
            }
            return errorResp;
        }
    }


}
