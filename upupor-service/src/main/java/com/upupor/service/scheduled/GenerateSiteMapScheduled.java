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

package com.upupor.service.scheduled;

import com.upupor.framework.CcRedisKey;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.scheduled.sitemap.AbstractSiteMap;
import com.upupor.service.scheduled.sitemap.enums.SiteMapType;
import com.upupor.service.utils.HtmlTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 定时任务
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/12 03:35
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateSiteMapScheduled {
    private final List<AbstractSiteMap<?>> abstractSiteMapList;

    /**
     * 每5分钟
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void scheduled() {
        log.info("定时任务执行检测,每5分钟打印一条日志");
    }

    /**
     * 每30分钟
     */
    @Scheduled(cron = "0 0/30 * * * ? ")
    public void googleSitemap() {
        log.info("生成站点地图任务启动");

        for (AbstractSiteMap<?> abstractSiteMap : abstractSiteMapList) {
            abstractSiteMap.doBusiness();
            List<GoogleSeoDto> sitemapList = abstractSiteMap.getGoogleSeoDtoList();
            if (CollectionUtils.isEmpty(sitemapList)) {
                continue;
            }

            render(sitemapList, abstractSiteMap.siteMapType());
        }

    }

    private void render(List<GoogleSeoDto> sitemapList, SiteMapType siteMapType) {
        Map<String, Object> params = new HashMap<>();
        params.put(CcTemplateConstant.GOOGLE_SEO_LIST, sitemapList);
        String s = HtmlTemplateUtils.renderGoogleSeoSiteMap(CcTemplateConstant.TEMPLATE_GOOGLE_SEO, params);
        if (StringUtils.isEmpty(s)) {
            return;
        }
        String siteMapKey = CcRedisKey.siteMapKey(siteMapType.name());
        RedisUtil.set(siteMapKey, s);
    }


}
