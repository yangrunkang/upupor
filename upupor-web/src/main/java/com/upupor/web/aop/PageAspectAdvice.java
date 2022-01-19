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
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.dao.entity.Slogan;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.SloganService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.types.PointType;
import com.upupor.service.types.SloganStatus;
import com.upupor.service.types.SloganType;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.web.aop.view_data.PrepareData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.*;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 02:29
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PageAspectAdvice {
    private final MemberService memberService;
    private final SloganService sloganService;
    private final ApplicationEventPublisher publisher;
    private final WhiteService whiteService;
    private final List<PrepareData> prepareDataList;



    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.upupor.web.page..*.*(..))")
    public void controllerLog() {}

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
        BuriedPointDataEvent pointEvent = BuriedPointDataEvent.builder()
                .request(request)
                .pointType(PointType.PAGE_REQUEST)
                .build();
        publisher.publishEvent(pointEvent);

        // 执行业务前
        String format = String.format("URL:%s", request.getRequestURL().toString());

        Object result = null;

        String servletPath = request.getServletPath();
        try {
            whiteService.pageCheck(servletPath);
            // 检查业务请求
            checkBusinessRequest(request, servletPath);

            // 调用业务方法
            result = proceedingJoinPoint.proceed();

            // 调用业务方法之后
            if (result instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) result;
                commonLogic(modelAndView, servletPath, startTime);
            }

        } catch (Exception exception) {
            exception.printStackTrace();

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName(CcConstant.PAGE_500);
            // 查看用户未读消息数量
            commonLogic(modelAndView, servletPath, startTime);
            if (exception instanceof BusinessException) {
                BusinessException businessException = (BusinessException) exception;
                modelAndView.addObject(CcConstant.GLOBAL_EXCEPTION, businessException.getMessage());
                // 如果是用户未登录要跳转至登录页
                if (businessException.getCode().equals(ErrorCode.USER_NOT_LOGIN.getCode())) {
                    String tips = "请登录";
                    modelAndView.addObject(CcConstant.TIPS_OPERATION_SHOULD_LOGIN, tips);
                    modelAndView.setViewName(CcConstant.USER_LOGIN);
                }
            } else {
                modelAndView.addObject(CcConstant.GLOBAL_EXCEPTION, exception.getMessage());
            }
            // 返回值要和你拦截的方法一致
            return modelAndView;
        }
        // 执行业务后
        format = format + String.format(" consume time:%s ms", getResponseTime(startTime));

        // 日志打印
        log.info(format);

        return result;
    }

    private void checkBusinessRequest(HttpServletRequest request, String servletPath) {
        if (StringUtils.isNotEmpty(servletPath)) {
            if (servletPath.startsWith("/continue-editor") || servletPath.startsWith("/editor") || servletPath.startsWith("/before-editor")) {
                Cookie[] cookies = request.getCookies();
                if (!ArrayUtils.isEmpty(cookies)) {
                    for (Cookie cookie : cookies) {
                        if ("isOpenEditor".equals(cookie.getName()) && "yes".equals(cookie.getValue())) {
                            throw new BusinessException(ErrorCode.EXISTS_IS_OPENING_EDITOR);
                        }
                    }
                }
            }
        }

        // 如果路径中包含 /user/admin,需要检验是否是管理员
        String adminUrl = "/user/admin";
        if (servletPath.startsWith(adminUrl)) {
            memberService.checkIsAdmin();
        }
    }

    /**
     * 统一逻辑
     */
    private void commonLogic(ModelAndView modelAndView, String servletPath, long startTime) {

        for (PrepareData prepareData : prepareDataList) {
            prepareData.prepare(modelAndView);
        }

        // 网页Slogan
        Slogan pageSlogan = pageSlogan(servletPath);
        modelAndView.addObject(SLOGAN, pageSlogan);
        // 设定响应时间
        modelAndView.addObject(RESPONSE_TIME, getResponseTime(startTime));
    }

    /**
     * sloganText依然生效
     *
     * @param servletPath
     * @return
     */
    private Slogan pageSlogan(String servletPath) {
        Slogan slogan = getPageSloganByPath(servletPath);
        // 统一使用自定义的样式,不在使用Slogan配置的样式
        if (Objects.nonNull(slogan)) {
            slogan.setBgStyle(getBgImg());
        } else {
            // thymeleaf html用判断,也是使用默认的
            slogan = new Slogan();
            slogan.setBgStyle(getBgImg());
        }

        return slogan;
    }

    private String getBgImg() {
        Object bgImg = ServletUtils.getSession().getAttribute(Session.USER_BG_IMG);
        if (Objects.isNull(bgImg)) {
            return null;
        }
        return bgImg.toString();
    }

    private Slogan getPageSloganByPath(String servletPath) {
        try {
            // 检查是否有,有则允许继续操作(类似于操作权限)
            int has = sloganService.countSloganPathByPath(servletPath);
            if (has > 0) {
                String[] split = servletPath.split(BACKSLASH);
                String path = StringUtils.join(split, DASH);
                if (StringUtils.isEmpty(path)) {
                    // 表明是首页
                    path = "/";
                }
                // 所有状态的Slogan
                List<Slogan> sloganList = sloganService.listByPath(path);
                if (CollectionUtils.isEmpty(sloganList)) {
                    // 入库
                    Slogan slogan = new Slogan();
                    slogan.setSloganId(CcUtils.getUuId());
                    slogan.setSloganName(path);
                    slogan.setSloganType(SloganType.PAGE);
                    slogan.setCreateTime(CcDateUtil.getCurrentTime());
                    slogan.setSloganStatus(SloganStatus.NORMAL);
                    slogan.setSysUpdateTime(new Date());
                    sloganService.addSlogan(slogan);
                } else {
                    Slogan slogan = sloganList.get(0);
                    if (Objects.isNull(slogan)) {
                        return null;
                    }
                    return slogan;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }



    public long getResponseTime(Long startTime) {
        return System.currentTimeMillis() - startTime;
    }

}
