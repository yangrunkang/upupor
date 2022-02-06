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

import com.alibaba.fastjson.JSON;
import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.business.aggregation.dao.entity.*;
import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.types.*;
import com.upupor.service.utils.HtmlTemplateUtils;
import com.upupor.service.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.upupor.framework.CcConstant.CvCache.SITE_MAP;
import static com.upupor.framework.CcConstant.CvCache.TAG_COUNT;


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

    private final ContentService contentService;
    private final MemberService memberService;
    private final TagService tagService;
    private final RadioService radioService;
    private final UpuporConfig upuporConfig;
    private final BusinessConfigService businessConfigService;

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


        List<GoogleSeoDto> googleSeoDtoList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // 生成文章内容的站点地图
        generateContentSiteMap(googleSeoDtoList, sdf);
        // 生成页面的站点地图
        generatePageSiteMap(googleSeoDtoList, sdf);
        // 生成用户首页和用户留言页
        generateMemberProfile(googleSeoDtoList, sdf);
        // 生成每一个标签页
        generateEveryTag(googleSeoDtoList, sdf);
        // 生成Tag云
        generateTagCloud(googleSeoDtoList, sdf);
        // 生成电台列表
        generateRadio(googleSeoDtoList, sdf);

        if (CollectionUtils.isEmpty(googleSeoDtoList)) {
            return;
        }

        // googleSeoList
        Map<String, Object> params = new HashMap<>();
        params.put(CcTemplateConstant.GOOGLE_SEO_LIST, googleSeoDtoList);
        String s = HtmlTemplateUtils.renderGoogleSeoSiteMap(CcTemplateConstant.TEMPLATE_GOOGLE_SEO, params);

        if (StringUtils.isEmpty(s)) {
            return;
        }

        RedisUtil.set(SITE_MAP, s);
    }

    private void generateRadio(List<GoogleSeoDto> googleSeoDtoList, SimpleDateFormat sdf) {
        // 文章总数
        Integer total = radioService.total();
        if (total <= 0) {
            log.error("文章总数为0");
            return;
        }

        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;
        List<Radio> radioAllList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            ListRadioDto listRadioDto = radioService.list(i + 1, CcConstant.Page.SIZE);
            if (CollectionUtils.isEmpty(listRadioDto.getRadioList())) {
                break;
            }
            radioAllList.addAll(listRadioDto.getRadioList());
        }

        if (CollectionUtils.isEmpty(radioAllList)) {
            log.error("无电台内容");
            return;
        }
        radioAllList.forEach(radio -> {
            if (radio.getStatus().equals(RadioStatus.NORMAL)) {
                GoogleSeoDto googleSeoDto = new GoogleSeoDto();
                // 按照参数顺序依次是 来源、内容Id
                String contentUrl = upuporConfig.getWebsite() + "/r/%s";
                contentUrl = String.format(contentUrl, radio.getRadioId());

                googleSeoDto.setLoc(contentUrl);
                googleSeoDto.setChangeFreq("hourly");
                googleSeoDto.setLastmod(sdf.format(radio.getSysUpdateTime()));
                googleSeoDto.setPriority("0.5");// 默认值
                googleSeoDtoList.add(googleSeoDto);
            }
        });

    }

    private void generateTagCloud(List<GoogleSeoDto> googleSeoDtoList, SimpleDateFormat sdf) {
        String s = RedisUtil.get(TAG_COUNT);
        List<CountTagDto> list = JSON.parseArray(s, CountTagDto.class);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (CountTagDto countTagDto : list) {
            GoogleSeoDto googleSeoDto = new GoogleSeoDto();
            googleSeoDto.setLoc(upuporConfig.getWebsite() + "/tag/" + countTagDto.getTagName());
            googleSeoDto.setChangeFreq("hourly");
            googleSeoDto.setLastmod(sdf.format(new Date()));
            googleSeoDto.setPriority("0.5");// 默认值
            googleSeoDtoList.add(googleSeoDto);
        }

    }

    /**
     * 生成每一个标签页
     *
     * @param googleSeoDtoList
     * @param sdf
     */
    private void generateEveryTag(List<GoogleSeoDto> googleSeoDtoList, SimpleDateFormat sdf) {
        List<Tag> techTagList = tagService.getTagsByType(ContentType.TECH);
        List<Tag> qaTagList = tagService.getTagsByType(ContentType.QA);
        List<Tag> shareTagList = tagService.getTagsByType(ContentType.SHARE);
        List<Tag> workplaceTagList = tagService.getTagsByType(ContentType.WORKPLACE);
        List<Tag> recordTagList = tagService.getTagsByType(ContentType.RECORD);

        renderTagUrl(googleSeoDtoList, techTagList, ContentType.TECH, sdf);
        renderTagUrl(googleSeoDtoList, qaTagList, ContentType.QA, sdf);
        renderTagUrl(googleSeoDtoList, shareTagList, ContentType.SHARE, sdf);
        renderTagUrl(googleSeoDtoList, workplaceTagList, ContentType.WORKPLACE, sdf);
        renderTagUrl(googleSeoDtoList, recordTagList, ContentType.RECORD, sdf);

    }

    private void renderTagUrl(List<GoogleSeoDto> googleSeoDtoList, List<Tag> tagList, ContentType type, SimpleDateFormat sdf) {
        if (CollectionUtils.isEmpty(tagList)) {
            return;
        }

        if (type.equals(ContentType.TECH)) {
            renderSeo(googleSeoDtoList, tagList, sdf, "tech");

        }

        if (type.equals(ContentType.QA)) {
            renderSeo(googleSeoDtoList, tagList, sdf, "qa");
        }

        if (type.equals(ContentType.SHARE)) {
            renderSeo(googleSeoDtoList, tagList, sdf, "share");
        }

        if (type.equals(ContentType.WORKPLACE)) {
            renderSeo(googleSeoDtoList, tagList, sdf, "workplace");
        }

        if (type.equals(ContentType.RECORD)) {
            renderSeo(googleSeoDtoList, tagList, sdf, "record");
        }

    }

    private void renderSeo(List<GoogleSeoDto> googleSeoDtoList, List<Tag> tagList, SimpleDateFormat sdf, String source) {
        tagList.forEach(tag -> {
            GoogleSeoDto tagSeo = new GoogleSeoDto();
            String tagUrl = upuporConfig.getWebsite() + "/" + source + "/%s";
            tagUrl = String.format(tagUrl, tag.getTagId());
            tagSeo.setLoc(tagUrl);
            tagSeo.setChangeFreq("hourly");
            tagSeo.setLastmod(sdf.format(new Date()));
            tagSeo.setPriority("0.5");// 默认值
            googleSeoDtoList.add(tagSeo);
        });
    }

    private void generateMemberProfile(List<GoogleSeoDto> googleSeoDtoList, SimpleDateFormat sdf) {
        // 文章总数
        Integer total = memberService.total();
        if (total <= 0) {
            log.error("短内容总数为0");
            return;
        }

        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            ListMemberDto listMemberDto = memberService.list(i + 1, CcConstant.Page.SIZE);
            if (CollectionUtils.isEmpty(listMemberDto.getMemberList())) {
                continue;
            }
            memberList.addAll(listMemberDto.getMemberList());
        }

        if (CollectionUtils.isEmpty(memberList)) {
            log.error("无用户");
            return;
        }
        memberList.forEach(member -> {
            if (member.getStatus().equals(MemberStatus.NORMAL)) {
                String webSite = upuporConfig.getWebsite();
                // 用户首页
                GoogleSeoDto memberProfile = new GoogleSeoDto();
                String memberProfileUrl = webSite + "/profile/%s/content";
                memberProfileUrl = String.format(memberProfileUrl, member.getUserId());
                memberProfile.setLoc(memberProfileUrl);
                memberProfile.setChangeFreq("hourly");
                memberProfile.setLastmod(sdf.format(member.getSysUpdateTime()));
                memberProfile.setPriority("0.5");// 默认值
                googleSeoDtoList.add(memberProfile);

                // 用户留言页
                GoogleSeoDto memberProfileMessage = new GoogleSeoDto();
                String memberProfileMessageUrl = webSite + "/profile/%s/message";
                memberProfileMessageUrl = String.format(memberProfileMessageUrl, member.getUserId());
                memberProfileMessage.setLoc(memberProfileMessageUrl);
                memberProfileMessage.setChangeFreq("hourly");
                memberProfileMessage.setLastmod(sdf.format(member.getSysUpdateTime()));
                memberProfileMessage.setPriority("0.5");// 默认值
                googleSeoDtoList.add(memberProfileMessage);
            }
        });

    }

    private void generatePageSiteMap(List<GoogleSeoDto> googleSeoDtoList, SimpleDateFormat sdf) {
        List<BusinessConfig> seoList = businessConfigService.listByBusinessConfigType(BusinessConfigType.SEO);
        if (CollectionUtils.isEmpty(seoList)) {
            return;
        }

        seoList.forEach(seo -> {
            GoogleSeoDto googleSeoDto = new GoogleSeoDto();
            googleSeoDto.setLoc(seo.getValue());
            googleSeoDto.setChangeFreq("hourly");
            googleSeoDto.setLastmod(sdf.format(seo.getSysUpdateTime()));
            googleSeoDto.setPriority("0.5");// 默认值
            googleSeoDtoList.add(googleSeoDto);
        });

    }


    private void generateContentSiteMap(List<GoogleSeoDto> googleSeoDtoList, SimpleDateFormat sdf) {
        // 文章总数
        Integer total = contentService.total();
        if (total <= 0) {
            log.error("文章总数为0");
            return;
        }

        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;
        List<Content> contentList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            List<Content> contents = contentService.list(i + 1, CcConstant.Page.SIZE);
            contentList.addAll(contents);
        }

        if (CollectionUtils.isEmpty(contentList)) {
            log.error("无文章内容");
            return;
        }
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
