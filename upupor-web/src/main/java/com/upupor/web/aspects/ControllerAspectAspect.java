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

package com.upupor.web.aspects;


import com.upupor.data.types.PointType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.JsonUtils;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.utils.JwtUtils;
import com.upupor.web.aspects.service.checker.controller.ControllerAspectChecker;
import com.upupor.web.aspects.service.checker.controller.dto.ControllerCheckerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.upupor.web.aspects.service.OrderConstant.CONTROLLER_ORDER;

/**
 * restful 接口切面
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 02:29
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(CONTROLLER_ORDER)
public class ControllerAspectAspect {
    private final ApplicationEventPublisher publisher;
    private final List<ControllerAspectChecker> controllerAspectCheckerList;

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.upupor.web.api..*.*(..))")
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
        StopWatch stopWatch = new StopWatch();
        CcUtils.start(stopWatch);

        CcResponse ccResponse = new CcResponse();
        HttpServletRequest request = JwtUtils.getRequest();
        String requestUrl = request.getRequestURL().toString();


        // 页面请求埋点
        BuriedPointDataEvent pointEvent = BuriedPointDataEvent.builder().request(request).pointType(PointType.DATA_REQUEST).build();
        publisher.publishEvent(pointEvent);

        try {
            for (ControllerAspectChecker checker : controllerAspectCheckerList) {
                checker.check(new ControllerCheckerDto(request, proceedingJoinPoint));
            }
            ccResponse.setData(proceedingJoinPoint.proceed());
        } catch (Throwable throwable) {
            log.error("[API] " + requestUrl + " 异常,打印发生移除的参数: {}", JsonUtils.toJsonStr(proceedingJoinPoint.getArgs()));
            throwable.printStackTrace();
            if (throwable instanceof BusinessException) {
                BusinessException businessException = (BusinessException) throwable;
                ccResponse.setCode(businessException.getCode());
                ccResponse.setData(businessException.getMessage());
            } else {
                ccResponse.setCode(ErrorCode.SYSTEM_ERROR.getCode());
                ccResponse.setData(throwable.getMessage());
            }
        }
        // 执行业务后
        String format = String.format("URL:%s\nconsume time:%s ms", requestUrl, CcUtils.stop(stopWatch));
        // 日志打印
        log.info(format);

        return ccResponse;
    }


}
