package com.upupor.service.service.aggregation;

import com.alibaba.fastjson.JSON;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dto.cache.CacheMemberDto;
import com.upupor.service.dto.page.CommonPageIndexDto;
import com.upupor.service.dto.page.common.ListBannerDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.aggregation.service.BannerService;
import com.upupor.service.service.aggregation.service.ContentService;
import com.upupor.service.service.aggregation.service.TagService;
import com.upupor.service.utils.RedisUtil;
import com.upupor.spi.req.GetCommonReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.CvCache.ACTIVE_USER_LIST;

/**
 * 公共服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 22:35
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAggregateService {

    private final ContentService contentService;
    private final TagService tagService;
    private final BannerService bannerService;

    public static HrefDesc getCreateContentInfo(Integer contentType, String tag) {
        if (Objects.isNull(contentType)) {
            return null;
        }
        return new HrefDesc(Objects.requireNonNull(CcEnum.ContentType.getByContentType(contentType)), tag);
    }

    public CommonPageIndexDto index(GetCommonReq getCommonReq) {
        if (Objects.isNull(getCommonReq.getPageNum())) {
            getCommonReq.setPageNum(CcConstant.Page.NUM);
        }

        if (Objects.isNull(getCommonReq.getPageSize())) {
            getCommonReq.setPageSize(CcConstant.Page.SIZE);
        }

        // 请求参数封装
        String tag = getTagId(getCommonReq);

        // 获取文章列表
        ListContentDto listContentDto = contentService.listContentByContentType(getCommonReq.getContentType(), getCommonReq.getPageNum(), getCommonReq.getPageSize(), tag);

        // 获取左边菜单栏list
        List<Tag> tagList = new ArrayList<>();
        if (Objects.nonNull(getCommonReq.getContentType()) && !CcEnum.ContentType.SHORT_CONTENT.getType().equals(getCommonReq.getContentType())) {
            tagList = tagService.getTagsByType(getCommonReq.getContentType());
        }

        // 活跃用户
        CacheMemberDto cacheMemberDto = new CacheMemberDto();
        String activeUserListJson = RedisUtil.get(ACTIVE_USER_LIST);
        if (!StringUtils.isEmpty(activeUserListJson)) {
            cacheMemberDto = JSON.parseObject(activeUserListJson, CacheMemberDto.class);
        }

        // 获取Banner栏
        ListBannerDto listBannerDto = bannerService.listBannerByStatus(CcEnum.BannerStatus.NORMAL.getStatus(), CcConstant.Page.NUM, CcConstant.Page.SIZE);

        CommonPageIndexDto commonPageIndexDto = new CommonPageIndexDto();
        commonPageIndexDto.setTagList(tagList);
        commonPageIndexDto.setMemberList(cacheMemberDto.getMemberList());
        commonPageIndexDto.setListContentDto(listContentDto);
        commonPageIndexDto.setListBannerDto(listBannerDto);
        commonPageIndexDto.setCurrentRootUrl(CcEnum.ContentType.getUrl(getCommonReq.getContentType()));
        commonPageIndexDto.setCreateContentDesc(getCreateContentInfo(getCommonReq.getContentType(), tag));
        return commonPageIndexDto;
    }

    private String getTagId(GetCommonReq getCommonReq) {
        if (!StringUtils.isEmpty(getCommonReq.getTagInId())) {
            return getCommonReq.getTagInId();
        }

        if (!StringUtils.isEmpty(getCommonReq.getTagId())) {
            return getCommonReq.getTagId();
        }

        return null;
    }

    @AllArgsConstructor
    @Data
    public static class HrefDesc {
        private String desc;
        private String href;
        private String icon;
        private String tips;

        public HrefDesc(CcEnum.ContentType contentType, String tag) {
            this.desc = contentType.getWebText();
            this.href = "/editor?type=" + contentType.getType();
            this.icon = contentType.getIcon();
            this.tips = contentType.getTips();
            this.href = "/editor?type=" + contentType.getType();
            if (!StringUtils.isEmpty(tag)) {
                this.href = this.getHref() + "&tag=" + tag;
            }
        }

    }


}
