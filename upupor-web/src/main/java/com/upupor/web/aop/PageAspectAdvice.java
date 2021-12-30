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
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.business.aggregation.service.SloganService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Slogan;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.scheduled.CountTagScheduled;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.RedisUtil;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.ListMessageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.upupor.service.common.CcConstant.*;
import static com.upupor.service.common.CcConstant.CvCache.TAG_COUNT;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 02:29
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PageAspectAdvice {

    private final MessageService messageService;
    private final MemberService memberService;
    private final SloganService sloganService;
    private final CountTagScheduled countTagScheduled;
    private final ContentService contentService;
    private final ApplicationEventPublisher publisher;
    private final WhiteService whiteService;


    /**
     * 当前激活的环境
     */
    @Value("${upupor.env}")
    private String activeEnv;

    /**
     * 当前激活的环境
     */
    @Value("${upupor.ossStatic}")
    private String ossStatic;

    /**
     * 是否开启广告
     */
    @Value("${upupor.ad-switch}")
    private String adSwitch;

    /**
     * 是否开启右侧广告
     */
    @Value("${upupor.ad-switch.right}")
    private String adSwitchRight;

    /**
     * 是否开启分析
     */
    @Value("${upupor.analyze-switch}")
    private String analyzeSwitch;

    /**
     * google广告ClientID
     */
    @Value("${upupor.googleAd.dataAdClientId}")
    private String dataAdClientId;

    /**
     * google广告ClientID
     */
    @Value("${upupor.googleAd.rightSlot}")
    private String rightSlot;

    /**
     * google广告ClientID
     */
    @Value("${upupor.googleAd.feedSlot}")
    private String feedSlot;


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
        BuriedPointDataEvent pointEvent = BuriedPointDataEvent.builder()
                .request(request)
                .pointType(CcEnum.PointType.PAGE_REQUEST.getType())
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
        // 设置网页title
        setUpuporLastTitle(modelAndView);
        // 获取未读消息
        getUserUnReadMessageCount(modelAndView);
        // 草稿数量
        draftCount(modelAndView);
        // 每日签到
        isDailyPoints(modelAndView);
        // 切入当前环境
        modelAndView.addObject(ACTIVE_ENV, activeEnv);
        modelAndView.addObject(OSS_STATIC, ossStatic);
        modelAndView.addObject(AD_SWITCH, adSwitch);
        modelAndView.addObject(AD_SWITCH_RIGHT, adSwitchRight);
        // 谷歌广告信息
        modelAndView.addObject(GoogleAd.CLIENT_ID, dataAdClientId);
        modelAndView.addObject(GoogleAd.RIGHT_SLOT, rightSlot);
        modelAndView.addObject(GoogleAd.FEED_SLOT, feedSlot);
        // 网页Slogan
        Slogan pageSlogan = pageSlogan(servletPath);
        modelAndView.addObject(SLOGAN, pageSlogan);
        // 分析开关
        modelAndView.addObject(ANALYZE_SWITCH, analyzeSwitch);
        // 使用分词来定义页面的关键字
        setKeyWords(modelAndView);
        // 检查是否有未完的文章
        checkContentIsDone(modelAndView);
        // 从缓存中获取根据标签获取文章数目的数据
        getTagCountFromRedis(modelAndView);
        // 设定响应时间
        modelAndView.addObject(RESPONSE_TIME, getResponseTime(startTime));
    }

    private void draftCount(ModelAndView modelAndView) {
        try {
            String userId = ServletUtils.getUserId();
            Integer draftCount = contentService.countDraft(userId);
            modelAndView.addObject(CcConstant.DRAFT_COUNT, draftCount);
        } catch (Exception e) {
            // 用户未登录异常 不处理
            modelAndView.addObject(CcConstant.UNREAD_MSG_COUNT, BigDecimal.ZERO);
        }
    }

    private void getTagCountFromRedis(ModelAndView modelAndView) {
        String s = RedisUtil.get(TAG_COUNT);
        Object result = JSON.parseObject(s, Object.class);
        if (Objects.isNull(result)) {
            modelAndView.addObject(CV_TAG_LIST, new ArrayList<>());
            // 刷新下
            countTagScheduled.refreshTag();
        }

        modelAndView.addObject(CV_TAG_LIST, result);
    }

    private void checkContentIsDone(ModelAndView modelAndView) {
        try {
            String cacheContentKey = CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId();
            String content = RedisUtil.get(cacheContentKey);
            if (!StringUtils.isEmpty(content)) {
                modelAndView.addObject(CONTENT_IS_DONE, Boolean.TRUE);
            }
        } catch (Exception e) {
            modelAndView.addObject(CONTENT_IS_DONE, Boolean.FALSE);
        }
    }

    private void setKeyWords(ModelAndView modelAndView) {
        Map<String, Object> model = modelAndView.getModel();
        if (!CollectionUtils.isEmpty(model)) {
            Object o = model.get(SeoKey.DESCRIPTION);
            if (o instanceof String) {
                // 视图名称是文章详情,则不设置keywords,由文章详情自己返回
                String viewName = modelAndView.getViewName();
                if (Objects.nonNull(viewName) && viewName.equals(CcConstant.CONTENT_INDEX)) {
                    return;
                }

                String description = (String) o;
                String segmentResult = CcUtils.getSegmentResult(description);
                modelAndView.addObject(SeoKey.KEYWORDS, segmentResult);
            }
        }
    }


    private void isDailyPoints(ModelAndView modelAndView) {
        // 今日是否签到
        modelAndView.addObject(DAILY_POINTS, memberService.checkIsGetDailyPoints());
    }

    private void setUpuporLastTitle(ModelAndView modelAndView) {
        // 将维度消息数显示在title
        Object title = modelAndView.getModelMap().getAttribute(SeoKey.TITLE);
        if (Objects.isNull(title)) {
            modelAndView.addObject(SeoKey.TITLE, "Upupor让每个人享受分享");
        }
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
                    slogan.setSloganType(CcEnum.SloganType.PAGE.getType());
                    slogan.setCreateTime(CcDateUtil.getCurrentTime());
                    slogan.setSloganStatus(CcEnum.SloganStatus.NORMAL.getStatus());
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
        } catch (Exception e) {
        }
        return null;
    }

    private void getUserUnReadMessageCount(ModelAndView modelAndView) {
        try {
            ListMessageReq listMessageReq = new ListMessageReq();
            listMessageReq.setUserId(ServletUtils.getUserId());
            listMessageReq.setStatus(CcEnum.MessageStatus.UN_READ.getStatus());
            Integer unReadCount = messageService.unReadMessageTotal(listMessageReq);
            modelAndView.addObject(CcConstant.UNREAD_MSG_COUNT, unReadCount);
            // 将维度消息数显示在title
            Object title = modelAndView.getModelMap().getAttribute(SeoKey.TITLE);
            if (!Objects.isNull(title)) {
                if (unReadCount > 0) {
                    modelAndView.addObject(SeoKey.TITLE, "(" + unReadCount + ")" + title);
                }
            }

        } catch (Exception e) {
            // 用户未登录异常 不处理
            modelAndView.addObject(CcConstant.UNREAD_MSG_COUNT, BigDecimal.ZERO);
        }
    }

    public long getResponseTime(Long startTime) {
        return System.currentTimeMillis() - startTime;
    }

}
