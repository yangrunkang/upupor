package com.upupor.service.scheduled;

import com.alibaba.fastjson.JSON;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.dao.entity.*;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.service.*;
import com.upupor.service.utils.HtmlTemplateUtils;
import com.upupor.service.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.upupor.service.common.CcConstant.CvCache.TAG_COUNT;


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


    private final SeoService seoService;

    private final MemberService memberService;

    private final TagService tagService;

    private final RadioService radioService;

    @Value("${upupor.website}")
    private String webSite;

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

        // 写到文件 upupor-google-sitemap.xml
        writeToFile(s);
        // 备份
        bakSeoData(s);

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
            if (radio.getStatus().equals(CcEnum.ContentStatus.NORMAL.getStatus())) {
                GoogleSeoDto googleSeoDto = new GoogleSeoDto();
                // 按照参数顺序依次是 来源、内容Id
                String contentUrl = webSite + "/r/%s";
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
            googleSeoDto.setLoc(webSite + "/tag/" + countTagDto.getTagName());
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
        List<Tag> techTagList = tagService.getTagsByType(CcEnum.TagType.TECH.getType());
        List<Tag> qaTagList = tagService.getTagsByType(CcEnum.TagType.QA.getType());
        List<Tag> shareTagList = tagService.getTagsByType(CcEnum.TagType.SHARE.getType());
        List<Tag> workplaceTagList = tagService.getTagsByType(CcEnum.TagType.WORKPLACE.getType());
        List<Tag> recordTagList = tagService.getTagsByType(CcEnum.TagType.RECORD.getType());

        renderTagUrl(googleSeoDtoList, techTagList, CcEnum.TagType.TECH.getType(), sdf);
        renderTagUrl(googleSeoDtoList, qaTagList, CcEnum.TagType.QA.getType(), sdf);
        renderTagUrl(googleSeoDtoList, shareTagList, CcEnum.TagType.SHARE.getType(), sdf);
        renderTagUrl(googleSeoDtoList, workplaceTagList, CcEnum.TagType.WORKPLACE.getType(), sdf);
        renderTagUrl(googleSeoDtoList, recordTagList, CcEnum.TagType.RECORD.getType(), sdf);

    }

    private void renderTagUrl(List<GoogleSeoDto> googleSeoDtoList, List<Tag> tagList, Integer type, SimpleDateFormat sdf) {
        if (CollectionUtils.isEmpty(tagList)) {
            return;
        }

        if (type.equals(CcEnum.TagType.TECH.getType())) {
            renderSeo(googleSeoDtoList, tagList, sdf, "tech");

        }

        if (type.equals(CcEnum.TagType.QA.getType())) {
            renderSeo(googleSeoDtoList, tagList, sdf, "qa");
        }

        if (type.equals(CcEnum.TagType.SHARE.getType())) {
            renderSeo(googleSeoDtoList, tagList, sdf, "share");
        }

        if (type.equals(CcEnum.TagType.WORKPLACE.getType())) {
            renderSeo(googleSeoDtoList, tagList, sdf, "workplace");
        }

        if (type.equals(CcEnum.TagType.RECORD.getType())) {
            renderSeo(googleSeoDtoList, tagList, sdf, "record");
        }

    }

    private void renderSeo(List<GoogleSeoDto> googleSeoDtoList, List<Tag> tagList, SimpleDateFormat sdf, String source) {
        tagList.forEach(tag -> {
            GoogleSeoDto tagSeo = new GoogleSeoDto();
            String tagUrl = webSite + "/" + source + "/%s";
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
            if (member.getStatus().equals(CcEnum.MemberStatus.NORMAL.getStatus())) {
                // 用户首页
                GoogleSeoDto memberProfile = new GoogleSeoDto();
                String memberProfileUrl = webSite + "/profile/%s";
                memberProfileUrl = String.format(memberProfileUrl, member.getUserId());
                memberProfile.setLoc(memberProfileUrl);
                memberProfile.setChangeFreq("hourly");
                memberProfile.setLastmod(sdf.format(member.getSysUpdateTime()));
                memberProfile.setPriority("0.5");// 默认值
                googleSeoDtoList.add(memberProfile);

                // 用户留言页
                GoogleSeoDto memberProfileMessage = new GoogleSeoDto();
                String memberProfileMessageUrl = webSite + "/profile-message/%s";
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
        List<Seo> seoList = seoService.listAll();
        if (CollectionUtils.isEmpty(seoList)) {
            return;
        }

        seoList.forEach(seo -> {
            GoogleSeoDto googleSeoDto = new GoogleSeoDto();
            googleSeoDto.setLoc(seo.getSeoContent());
            googleSeoDto.setChangeFreq("hourly");
            googleSeoDto.setLastmod(sdf.format(seo.getSysUpdateTime()));
            googleSeoDto.setPriority("0.5");// 默认值
            googleSeoDtoList.add(googleSeoDto);
        });

    }


    private void writeToFile(String s) {
        BufferedWriter out = null;
        try {
            // 这个文件是写到了打包后的classes目录下的文件,开发环境看不到
            File file = ResourceUtils.getFile("classpath:static/upupor-google-sitemap.xml");
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    log.error("upupor-google-sitemap.xml文件创建失败");
                }
            }
            out = new BufferedWriter(new FileWriter(file));
            out.write(s);
            out.flush();
            out.close();
            log.info("Google站点地图生成完毕");
        } catch (Exception e) {
            log.error("写入站点地图失败,{}", e.getMessage());
        } finally {
            try {
                if (Objects.nonNull(out)) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void bakSeoData(String s) {
        try {
            // 生成Google站点地图
            Seo google = seoService.getBySeoId("google");
            if (Objects.isNull(google)) {
                Seo seo = new Seo();
                seo.setSeoId("google");
                seo.setCreateTime(CcDateUtil.getCurrentTime());
                seo.setSeoStatus(CcEnum.SeoStatus.NORMAL.getStatus());
                seo.setSysUpdateTime(new Date());
                seo.setSeoContent(s);
                Boolean addSeo = seoService.addSeo(seo);
                if (!addSeo) {
                    log.error("添加Google Seo信息失败");
                }
            } else {
                google.setSeoContent(s);
                Boolean update = seoService.updateSeo(google);
                if (!update) {
                    log.error("更新Google Seo信息失败");
                }
            }
        } catch (Exception e) {

        }
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
            if (content.getStatus().equals(CcEnum.ContentStatus.NORMAL.getStatus())) {
                GoogleSeoDto googleSeoDto = new GoogleSeoDto();
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
