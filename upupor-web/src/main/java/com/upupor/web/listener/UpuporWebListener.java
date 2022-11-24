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

package com.upupor.web.listener;

import com.upupor.framework.CcRedisKey;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.business.task.TaskService;
import com.upupor.service.business.task.sitemap.enums.SiteMapType;
import com.upupor.service.business.task.sitemap.model.SiteDataModel;
import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.dto.email.EmailTemplateReplaceAndSendEvent;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.listener.event.GenerateGoogleSiteMapEvent;
import com.upupor.service.listener.event.RenderSiteMapEvent;
import com.upupor.web.utils.CcEmailUtils;
import com.upupor.web.utils.HtmlTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年11月24日
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UpuporWebListener {
    private final TaskService taskService;

    @EventListener
    @Async
    public void generateGoogleSiteMapEvent(GenerateGoogleSiteMapEvent event) {
        log.info("程序启动完毕,Event Bus事件,开始生成Google站点地图");
        taskService.googleSitemap();
    }

    @EventListener
    @Async
    public void renderSiteMapEvent(RenderSiteMapEvent event) {
        List<SiteDataModel> siteDataModelList = event.getSiteDataModelList();
        if (CollectionUtils.isEmpty(siteDataModelList)) {
            return;
        }
        for (SiteDataModel siteDataModel : siteDataModelList) {
            renderSiteMapXML(siteDataModel.getSitemapList(), siteDataModel.getSiteMapType());
        }
    }

    private void renderSiteMapXML(List<GoogleSeoDto> sitemapList, SiteMapType siteMapType) {
        Map<String, Object> params = new HashMap<>();
        params.put(CcTemplateConstant.GOOGLE_SEO_LIST, sitemapList);
        String s = HtmlTemplateUtils.renderGoogleSeoSiteMap(CcTemplateConstant.TEMPLATE_GOOGLE_SEO, params);
        if (StringUtils.isEmpty(s)) {
            return;
        }
        String siteMapKey = CcRedisKey.siteMapKey(siteMapType.name());
        RedisUtil.set(siteMapKey, s);
    }

    /**
     * 正式发送邮件
     *
     * @param templateReplaceAndSendEvent
     */
    @EventListener
    @Async
    public void trueSend(EmailTemplateReplaceAndSendEvent templateReplaceAndSendEvent) {
        CcEmailUtils.sendEmail(templateReplaceAndSendEvent);
    }
}
