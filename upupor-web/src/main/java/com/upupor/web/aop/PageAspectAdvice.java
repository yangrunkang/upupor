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

package com.upupor.web.aop;

import com.upupor.framework.CcConstant;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.types.PointType;
import com.upupor.web.aop.checker.page.PageAspectChecker;
import com.upupor.web.aop.checker.page.dto.PageCheckDto;
import com.upupor.web.aop.view.PrepareData;
import com.upupor.web.aop.view.ViewData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.upupor.framework.CcConstant.UserView.USER_LOGIN;
import static com.upupor.framework.utils.CcDateUtil.getResponseTime;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 02:29
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PageAspectAdvice {
    private final ApplicationEventPublisher publisher;
    private final List<PrepareData> prepareDataList;
    private final List<PageAspectChecker> pageAspectCheckerList;

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.upupor.web.page..*.*(..))")
    public void controllerLog() {
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 页面请求埋点
        BuriedPointDataEvent pointEvent = BuriedPointDataEvent.builder().request(request).pointType(PointType.PAGE_REQUEST).build();
        publisher.publishEvent(pointEvent);

        Object result;

        String servletPath = request.getServletPath();
        try {
            // 检查业务请求
            for (PageAspectChecker checker : pageAspectCheckerList) {
                checker.check(new PageCheckDto(request));
            }

            // 调用业务方法
            result = proceedingJoinPoint.proceed();

            // 调用业务方法之后
            if (result instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) result;
                setViewData(modelAndView, servletPath, startTime);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return exceptionView(startTime, servletPath, exception);
        }

        // 执行业务后
        String format = String.format("URL:%s \nconsume time:%s ms", request.getRequestURL().toString(), getResponseTime(startTime));
        // 日志打印
        log.info(format);

        return result;
    }

    /**
     * 异常视图
     * @param startTime
     * @param servletPath
     * @param exception
     * @return
     */
    private ModelAndView exceptionView(long startTime, String servletPath, Exception exception) {
        ModelAndView exceptionView = new ModelAndView();
        exceptionView.setViewName(CcConstant.PAGE_500);
        setViewData(exceptionView, servletPath, startTime);
        if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            exceptionView.addObject(CcConstant.GLOBAL_EXCEPTION, businessException.getMessage());
            // 如果是用户未登录要跳转至登录页
            if (businessException.getCode().equals(ErrorCode.USER_NOT_LOGIN.getCode())) {
                String tips = "请登录";
                exceptionView.addObject(CcConstant.TIPS_OPERATION_SHOULD_LOGIN, tips);
                exceptionView.setViewName(USER_LOGIN);
            }
        } else {
            exceptionView.addObject(CcConstant.GLOBAL_EXCEPTION, exception.getMessage());
        }

        // 返回值要和你拦截的方法一致
        return exceptionView;
    }


    /**
     * 统一逻辑
     */
    private void setViewData(ModelAndView modelAndView, String servletPath, long startTime) {
        ViewData viewData = ViewData.create(modelAndView, servletPath, startTime);
        for (PrepareData prepareData : prepareDataList) {
            prepareData.prepare(viewData);
        }
    }
}
