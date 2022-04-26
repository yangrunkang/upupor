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

package com.upupor.service.scheduled.sitemap;

import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.data.dao.entity.Tag;
import com.upupor.service.data.aggregation.service.TagService;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.types.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年03月13日 21:30
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class ContentTypeTagSiteMap extends AbstractSiteMap<Tag> {
    private final TagService tagService;
    private final UpuporConfig upuporConfig;

    @Override
    protected Boolean dataCheck() {
        return Boolean.TRUE;
    }

    @Override
    protected List<Tag> getSiteMapData() {

        List<Tag> tagList = new ArrayList<>();

        List<Tag> techTagList = tagService.getTagsByType(ContentType.TECH);
        List<Tag> qaTagList = tagService.getTagsByType(ContentType.QA);
        List<Tag> shareTagList = tagService.getTagsByType(ContentType.SHARE);
        List<Tag> workplaceTagList = tagService.getTagsByType(ContentType.WORKPLACE);
        List<Tag> recordTagList = tagService.getTagsByType(ContentType.RECORD);

        tagList.addAll(techTagList);
        tagList.addAll(qaTagList);
        tagList.addAll(shareTagList);
        tagList.addAll(workplaceTagList);
        tagList.addAll(recordTagList);
        return tagList;
    }

    @Override
    protected void renderSiteMap(List<Tag> tagList) {
        tagList.forEach(tag -> {
            GoogleSeoDto tagSeo = new GoogleSeoDto();
            String tagUrl = upuporConfig.getWebsite() + tag.getTagType().getUrl() + "/%s";
            tagUrl = String.format(tagUrl, tag.getTagId());
            tagSeo.setLoc(tagUrl);
            tagSeo.setChangeFreq("hourly");
            tagSeo.setLastmod(sdf.format(new Date()));
            tagSeo.setPriority("0.5");// 默认值
            googleSeoDtoList.add(tagSeo);
        });
    }

}

