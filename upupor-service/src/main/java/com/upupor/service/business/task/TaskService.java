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

package com.upupor.service.business.task;

import com.upupor.data.dao.entity.BusinessConfig;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dto.cache.CacheMemberDto;
import com.upupor.data.dto.page.common.CountTagDto;
import com.upupor.data.dto.seo.GoogleSeoDto;
import com.upupor.data.types.BusinessConfigType;
import com.upupor.framework.CcRedisKey;
import com.upupor.framework.utils.JsonUtils;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.security.sensitive.SensitiveWord;
import com.upupor.service.base.BusinessConfigService;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.MemberService;
import com.upupor.service.business.task.sitemap.AbstractSiteMap;
import com.upupor.service.business.task.sitemap.model.SiteDataModel;
import com.upupor.service.listener.event.RenderSiteMapEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.upupor.framework.CcRedisKey.ACTIVE_USER_LIST;
import static com.upupor.framework.CcRedisKey.CACHE_SENSITIVE_WORD;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年11月24日
 * @email: yangrunkang53@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final List<AbstractSiteMap<?>> abstractSiteMapList;
    private final MemberService memberService;
    private final ContentService contentService;
    private final BusinessConfigService businessConfigService;
    private final ApplicationEventPublisher eventPublisher;

    public void googleSitemap() {
        log.info("生成站点地图任务启动");
        List<SiteDataModel> siteDataModelList = new ArrayList<>();

        for (AbstractSiteMap<?> abstractSiteMap : abstractSiteMapList) {
            abstractSiteMap.doBusiness();
            List<GoogleSeoDto> sitemapList = abstractSiteMap.googleSeoDtoList;
            if (CollectionUtils.isEmpty(sitemapList)) {
                continue;
            }
            siteDataModelList.add(SiteDataModel.builder()
                    .sitemapList(sitemapList)
                    .siteMapType(abstractSiteMap.siteMapType())
                    .build());
        }

        eventPublisher.publishEvent(RenderSiteMapEvent.builder().siteDataModelList(siteDataModelList).build());
    }

    public void refreshActiveMember() {
        log.info("统计活跃用户任务启动");
        List<MemberEnhance> memberEnhanceList = memberService.activeMember();
        if (CollectionUtils.isEmpty(memberEnhanceList)) {
            memberEnhanceList = new ArrayList<>();
        }
        CacheMemberDto cacheMemberDto = new CacheMemberDto();
        cacheMemberDto.setMemberEnhanceList(memberEnhanceList);
        RedisUtil.set(ACTIVE_USER_LIST, JsonUtils.toJsonStr(cacheMemberDto));
    }

    public void refreshSensitiveWord() {
        List<BusinessConfig> businessConfigList = businessConfigService.listByBusinessConfigType(BusinessConfigType.SENSITIVE_WORD);
        if (CollectionUtils.isEmpty(businessConfigList)) {
            return;
        }
        List<String> wordList = businessConfigList.stream().map(BusinessConfig::getValue)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWordList(wordList);
        RedisUtil.set(CACHE_SENSITIVE_WORD, JsonUtils.toJsonStr(sensitiveWord));
        log.info("刷新敏感词完成");
    }

    public void refreshTag() {
        log.info("刷新标签云任务启动");

        List<CountTagDto> countTagDtos = contentService.listAllTag();
        if (CollectionUtils.isEmpty(countTagDtos)) {
            return;
        }

        RedisUtil.set(CcRedisKey.TAG_COUNT, JsonUtils.toJsonStr(countTagDtos));
    }
}
