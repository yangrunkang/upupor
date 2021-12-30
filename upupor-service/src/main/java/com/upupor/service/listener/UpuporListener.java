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

package com.upupor.service.listener;

import com.upupor.service.business.aggregation.service.BuriedPointDataService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.CcConstant;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.listener.event.GenerateGoogleSiteMapEvent;
import com.upupor.service.scheduled.GenerateSiteMapScheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Upupor 监听器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 11:47
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UpuporListener {

    private final BuriedPointDataService buriedPointDataService;

    private final GenerateSiteMapScheduled generateSiteMapScheduled;

    private final MemberService memberService;

    /**
     * 埋点
     *
     * @param event
     */
    @EventListener
    @Async
    public void buriedPointData(BuriedPointDataEvent event) {
        HttpServletRequest request = event.getRequest();
        HttpSession session = request.getSession();

        Object userIdAttribute = session.getAttribute(CcConstant.Session.USER_ID);
        String userId;
        if (!Objects.isNull(userIdAttribute)) {
            userId = String.valueOf(userIdAttribute);
        } else {
            String requestedSessionId = request.getRequestedSessionId();
            if (StringUtils.isEmpty(requestedSessionId)) {
                userId = "Start";
            } else {
                userId = requestedSessionId;
            }
        }

        //检测是否是userId
        if (org.apache.commons.lang3.StringUtils.isNumeric(userId)) {
            memberService.updateActiveTime(userId);
        }

    }

    @EventListener
    @Async
    public void generateGoogleSiteMapEvent(GenerateGoogleSiteMapEvent event) {
        log.info("程序启动完毕,Event Bus事件,开始生成Google站点地图");
        generateSiteMapScheduled.googleSitemap();
    }

}
