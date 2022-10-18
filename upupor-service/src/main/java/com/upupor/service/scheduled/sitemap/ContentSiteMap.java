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

package com.upupor.service.scheduled.sitemap;

import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.scheduled.ScheduledCommonService;
import com.upupor.service.scheduled.sitemap.enums.SiteMapType;
import com.upupor.service.types.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author Yang Runkang (cruise)
 * @date 2022年03月13日 21:29
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class ContentSiteMap extends AbstractSiteMap<Content> {
    private final ContentService contentService;
    private final UpuporConfig upuporConfig;
    private final ScheduledCommonService scheduledCommonService;

    @Override
    protected Boolean dataCheck() {
        return contentService.total() > 0;
    }

    @Override
    public SiteMapType siteMapType() {
        return SiteMapType.CONTENT;
    }

    @Override
    protected List<Content> getSiteMapData() {
        return scheduledCommonService.contentList();
    }

    @Override
    protected void renderSiteMap(List<Content> contentList) {
        contentList.forEach(content -> {
            if (content.getStatus().equals(ContentStatus.NORMAL)) {
                GoogleSeoDto googleSeoDto = new GoogleSeoDto();
                String webSite = upuporConfig.getWebsite();
                // 按照参数顺序依次是 来源、内容Id
                String contentUrl = webSite + "/u/%s";
                contentUrl = String.format(contentUrl, content.getContentId());

                googleSeoDto.setLoc(contentUrl);
                googleSeoDto.setChangeFreq("hourly");
                googleSeoDto.setLastmod(sdf.format(content.getSysUpdateTime()));
                googleSeoDto.setPriority("0.5");// 默认值
                googleSeoDtoList.add(googleSeoDto);
            }
        });
    }
}
