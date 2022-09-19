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

package com.upupor.service.data.aggregation;

import com.alibaba.fastjson.JSON;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Tag;
import com.upupor.service.data.service.BannerService;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.data.service.TagService;
import com.upupor.service.dto.cache.CacheMemberDto;
import com.upupor.service.dto.page.CommonPageIndexDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListBannerDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.GetCommonReq;
import com.upupor.service.types.BannerStatus;
import com.upupor.service.types.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.upupor.framework.CcConstant.CvCache.ACTIVE_USER_LIST;

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

    public static HrefDesc getCreateContentInfo(ContentType contentType, String tag, String tagName) {
        if (Objects.isNull(contentType)) {
            return null;
        }
        return new HrefDesc(Objects.requireNonNull(contentType), tag, tagName);
    }

    public static HrefDesc getCreateContentInfo(ContentType contentType, String tag) {
        if (Objects.isNull(contentType)) {
            return null;
        }
        return new HrefDesc(Objects.requireNonNull(contentType), tag, null);
    }

    public CommonPageIndexDto index(GetCommonReq getCommonReq) {
        if (Objects.isNull(getCommonReq.getPageNum())) {
            getCommonReq.setPageNum(CcConstant.Page.NUM);
        }

        if (Objects.isNull(getCommonReq.getPageSize())) {
            getCommonReq.setPageSize(CcConstant.Page.SIZE);
        }

        // 请求参数封装
        String tag = getCommonReq.getTagId();

        // 获取文章列表
        CompletableFuture<ListContentDto> listContentDtoFuture = CompletableFuture.supplyAsync(() -> {
            ListContentDto listContentDto = contentService.listContentByContentType(getCommonReq.getContentType(), getCommonReq.getPageNum(), getCommonReq.getPageSize(), tag);
            // 如果点击了[技术]-[SEO]中的二级分类(例如:SEO),则不设置tagDtoList,不然界面上都是二级分类的标识
            if (!StringUtils.isEmpty(getCommonReq.getTagId()) && !CollectionUtils.isEmpty(listContentDto.getContentList())) {
                listContentDto.getContentList().forEach(s -> s.setTagDtoList(Lists.newArrayList()));
            }
            return listContentDto;
        });
        // 最近一周新增的文章
        CompletableFuture<List<Content>> latestContentListFuture = CompletableFuture.supplyAsync(contentService::latestContentList);
        // 获取Banner栏
        CompletableFuture<ListBannerDto> listBannerDtoFuture = CompletableFuture.supplyAsync(() -> bannerService.listBannerByStatus(BannerStatus.NORMAL, CcConstant.Page.NUM, CcConstant.Page.SIZE));

        // 获取左边菜单栏list
        CompletableFuture<List<Tag>> tagListFuture = CompletableFuture.supplyAsync(() -> {
            List<Tag> tagList = new ArrayList<>();
            if (Objects.nonNull(getCommonReq.getContentType())) {
                tagList = tagService.getTagsByType(getCommonReq.getContentType());
            }
            if (!CollectionUtils.isEmpty(tagList)) {
                List<String> tagIdList = tagList.stream().map(Tag::getTagId).collect(Collectors.toList());
                List<CountTagDto> countTagDtos = contentService.listCountByTagIds(tagIdList);
                tagList.forEach(tagItem -> countTagDtos.forEach(countTagDto -> {
                    if (tagItem.getTagId().equals(countTagDto.getTagId())) {
                        tagItem.setCount(countTagDto.getCount());
                    }
                }));
                tagList.sort(Comparator.comparingInt(Tag::getCount).reversed());
            }
            return tagList;
        });

        // 活跃用户
        CompletableFuture<CacheMemberDto> cacheMemberDtoFuture = CompletableFuture.supplyAsync(() -> {
            CacheMemberDto cacheMemberDto = new CacheMemberDto();
            String activeUserListJson = RedisUtil.get(ACTIVE_USER_LIST);
            if (!StringUtils.isEmpty(activeUserListJson)) {
                cacheMemberDto = JSON.parseObject(activeUserListJson, CacheMemberDto.class);
            }
            return cacheMemberDto;
        });

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(
                listContentDtoFuture,
                tagListFuture,
                cacheMemberDtoFuture,
                latestContentListFuture,
                listBannerDtoFuture
        );
        allFuture.join();

        CommonPageIndexDto commonPageIndexDto = new CommonPageIndexDto(Boolean.FALSE);
        try {
            commonPageIndexDto.setTagList(tagListFuture.get());
            commonPageIndexDto.setMemberList(cacheMemberDtoFuture.get().getMemberList());
            commonPageIndexDto.setListContentDto(listContentDtoFuture.get());
            commonPageIndexDto.setListBannerDto(listBannerDtoFuture.get());
            commonPageIndexDto.setCurrentRootUrl(ContentType.getUrl(getCommonReq.getContentType()));
            commonPageIndexDto.setCreateContentDesc(getCreateContentInfo(getCommonReq.getContentType(), tag, tagService.getNameById(tag)));
            commonPageIndexDto.setLatestContentList(latestContentListFuture.get());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYNC_FETCH_DATA_ERROR);
        }
        return commonPageIndexDto;
    }

    @AllArgsConstructor
    @Data
    public static class HrefDesc {
        private String desc;
        private String href;
        private String icon;
        private String tips;

        public HrefDesc(ContentType contentType, String tag, String tagName) {
            this.desc = contentType.getTips();
            this.href = "/editor?type=" + contentType.name();
            UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
            this.icon = upuporConfig.getOssStaticPrefix() + contentType.getIcon();
            this.tips = contentType.getTips();
            this.href = "/editor?type=" + contentType.name();
            if (!StringUtils.isEmpty(tag)) {
                this.href = this.getHref() + "&tag=" + tag;
            }
            if (!StringUtils.isEmpty(tagName)) {
                this.desc = contentType.getTips() + " >> " + tagName;
            }
        }

    }


}
