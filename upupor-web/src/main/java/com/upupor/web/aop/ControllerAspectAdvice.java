/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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


import com.alibaba.fastjson.JSON;
import com.upupor.framework.utils.ValidationUtil;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcResponse;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.types.PointType;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 02:29
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerAspectAdvice {
    private final ApplicationEventPublisher publisher;
    private final MemberService memberService;
    private final WhiteService whiteService;

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.upupor.web.controller..*.*(..))")
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
        // 服务统一响应对象
        CcResponse ccResponse = new CcResponse();

        long startTime = System.currentTimeMillis();

        HttpServletRequest request = ServletUtils.getRequest();

        // 执行业务前
        String format = String.format("URL:%s", request.getRequestURL().toString());

        // 页面请求埋点
        BuriedPointDataEvent pointEvent = BuriedPointDataEvent.builder()
                .request(request)
                .pointType(PointType.DATA_REQUEST)
                .build();
        publisher.publishEvent(pointEvent);

        String servletPath = request.getServletPath();


        Object result = null;
        try {
            // 如果路径中包含 /admin,需要检验是否是管理员
            String adminUrl = "/admin";
            if (servletPath.startsWith(adminUrl)) {
                memberService.checkIsAdmin();
            }

            // 如果不在白名单中,需要检验是否登录
            whiteService.interfaceWhiteCheck(servletPath);

            // 参数校验
            Object[] args = proceedingJoinPoint.getArgs();
            for (Object arg : args) {
                ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(arg);
                if (!CollectionUtils.isEmpty(validResult.getAllErrors())) {
                    // 格式化显示错误信息
                    List<ValidationUtil.ErrorMessage> allErrors = validResult.getAllErrors();
                    StringBuilder sb = new StringBuilder();
                    sb.append("错误信息:").append(CcConstant.BREAK_LINE);
                    for (int i = 0; i < allErrors.size(); i++) {
                        sb.append(i + 1).append(CcConstant.ONE_DOTS).append(allErrors.get(i).getMessage()).append(CcConstant.BREAK_LINE);
                    }
                    throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), sb.toString());
                }
            }
            result = proceedingJoinPoint.proceed();
            ccResponse.setData(result);
        } catch (Throwable throwable) {
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
        format = format + String.format("\nresponse:%s\nconsume time:%s ms", JSON.toJSONString(result), System.currentTimeMillis() - startTime);

        // 日志打印
        log.info(format);

        return ccResponse;
    }


}
