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

package com.upupor.service.aggregation;

import com.upupor.data.dao.entity.Tag;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dao.entity.enhance.TagEnhance;
import com.upupor.data.dto.cache.CacheMemberDto;
import com.upupor.data.dto.page.CommonPageIndexDto;
import com.upupor.data.dto.page.HrefDesc;
import com.upupor.data.dto.page.common.CountTagDto;
import com.upupor.data.dto.page.common.ListBannerDto;
import com.upupor.data.dto.page.common.ListContentDto;
import com.upupor.data.types.BannerStatus;
import com.upupor.data.types.ContentType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcRedis;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.JsonUtils;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.base.BannerService;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.TagService;
import com.upupor.service.outer.req.GetCommonReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


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
            if (!StringUtils.isEmpty(getCommonReq.getTagId()) && !CollectionUtils.isEmpty(listContentDto.getContentEnhanceList())) {
                listContentDto.removeTag();
            }
            return listContentDto;
        });
        // 最近一周新增的文章
        CompletableFuture<List<ContentEnhance>> latestContentListFuture = CompletableFuture.supplyAsync(contentService::latestContentList);
        // 获取Banner栏
        CompletableFuture<ListBannerDto> listBannerDtoFuture = CompletableFuture.supplyAsync(() -> bannerService.listBannerByStatus(BannerStatus.NORMAL, CcConstant.Page.NUM, CcConstant.Page.SIZE));

        // 获取左边菜单栏list
        CompletableFuture<List<TagEnhance>> tagListFuture = CompletableFuture.supplyAsync(() -> {
            List<TagEnhance> tagEnhanceList = new ArrayList<>();
            if (Objects.nonNull(getCommonReq.getContentType())) {
                tagEnhanceList = tagService.getTagsByType(getCommonReq.getContentType());
            }
            if (!CollectionUtils.isEmpty(tagEnhanceList)) {
                List<String> tagIdList = tagEnhanceList.stream()
                        .map(TagEnhance::getTag)
                        .map(Tag::getTagId).
                        collect(Collectors.toList());

                List<CountTagDto> countTagDtos = contentService.listCountByTagIds(tagIdList);
                tagEnhanceList.forEach(tagEnhance -> countTagDtos.forEach(countTagDto -> {
                    if (tagEnhance.getTag().getTagId().equals(countTagDto.getTagId())) {
                        tagEnhance.setCount(countTagDto.getCount());
                    }
                }));
                tagEnhanceList.sort(Comparator.comparingInt(TagEnhance::getCount).reversed());
            }
            return tagEnhanceList;
        });

        // 活跃用户
        CompletableFuture<CacheMemberDto> cacheMemberDtoFuture = CompletableFuture.supplyAsync(() -> {
            CacheMemberDto cacheMemberDto = new CacheMemberDto();
            String activeUserListJson = RedisUtil.get(CcRedis.Key.ACTIVE_USER_LIST);
            if (!StringUtils.isEmpty(activeUserListJson)) {
                cacheMemberDto = JsonUtils.parse2Clazz(activeUserListJson, CacheMemberDto.class);
                // 因为活跃用户列表是缓存的,会存在延后,所有这里重新根据Redis里面的最新数据覆盖
                for (MemberEnhance memberEnhance : cacheMemberDto.getMemberEnhanceList()) {
                    memberEnhance.setLastActiveTime(CcRedis.Key.userActiveKey(memberEnhance.getMember().getUserId()));
                }
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
            commonPageIndexDto.setTagEnhanceList(tagListFuture.get());
            commonPageIndexDto.setMemberEnhanceList(cacheMemberDtoFuture.get().getMemberEnhanceList());
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


}
