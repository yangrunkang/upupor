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

import com.upupor.service.data.dao.entity.BusinessConfig;
import com.upupor.service.data.service.BusinessConfigService;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.scheduled.sitemap.enums.SiteMapType;
import com.upupor.service.types.BusinessConfigType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年03月13日 21:30
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class PageSiteMap extends AbstractSiteMap<BusinessConfig> {

    private final BusinessConfigService businessConfigService;

    @Override
    protected Boolean dataCheck() {
        return !CollectionUtils.isEmpty(businessConfigService.listByBusinessConfigType(BusinessConfigType.SEO));
    }

    @Override
    public SiteMapType siteMapType() {
        return SiteMapType.PAGE;
    }

    @Override
    protected List<BusinessConfig> getSiteMapData() {
        return businessConfigService.listByBusinessConfigType(BusinessConfigType.SEO);
    }

    @Override
    protected void renderSiteMap(List<BusinessConfig> businessConfigList) {
        businessConfigList.forEach(seo -> {
            GoogleSeoDto googleSeoDto = new GoogleSeoDto();
            googleSeoDto.setLoc(seo.getValue());
            googleSeoDto.setChangeFreq("hourly");
            googleSeoDto.setLastmod(sdf.format(seo.getSysUpdateTime()));
            googleSeoDto.setPriority("0.5");// 默认值
            googleSeoDtoList.add(googleSeoDto);
        });
    }
}