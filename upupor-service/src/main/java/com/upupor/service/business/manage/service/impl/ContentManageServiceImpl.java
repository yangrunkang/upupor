/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.service.business.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.manage.service.ContentManageService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.mapper.ContentMapper;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.spi.req.ListContentReq;
import com.upupor.service.types.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 内容管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:09
 */
@Service
@RequiredArgsConstructor
public class ContentManageServiceImpl implements ContentManageService {
    private final ContentMapper contentMapper;

    private final ContentService contentService;

    @Override
    public ListContentDto listContent(ListContentReq listContentReq) {
        if (Strings.isEmpty(listContentReq.getUserId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        return getListContentDto(listContentReq);
    }

    @Override
    public ListContentDto listContentDraft(Integer pageNum, Integer pageSize, String userId, String searchTitle) {
        if (Strings.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        return getListContentDtoDraft(pageNum, pageSize, userId, searchTitle);
    }

    private ListContentDto getListContentDto(ListContentReq listContentReq) {

        LambdaQueryWrapper<Content> listQuery = new LambdaQueryWrapper<Content>()
                .notIn(Content::getStatus, ContentStatus.notIn())
                .orderByDesc(Content::getCreateTime);
        if (StringUtils.isNotEmpty(listContentReq.getUserId())) {
            listQuery.eq(Content::getUserId, listContentReq.getUserId());
        }
        if (StringUtils.isNotEmpty(listContentReq.getContentId())) {
            listQuery.eq(Content::getContentId, listContentReq.getContentId());
        }
        bindSearchTitle(listContentReq.getSearchTitle(), listQuery);
        if (Objects.nonNull(listContentReq.getSelect()) && "ONLY_SELF_SEE".equals(listContentReq.getSelect())) {
            listQuery.eq(Content::getStatus, ContentStatus.ONLY_SELF_CAN_SEE);
        }

        PageHelper.startPage(listContentReq.getPageNum(), listContentReq.getPageSize());
        List<Content> contents = contentMapper.selectList(listQuery);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        contentService.bindContentMember(contents);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }

    private void bindSearchTitle(String searchTitle, LambdaQueryWrapper<Content> listQuery) {
        if (StringUtils.isNotEmpty(searchTitle)) {
            listQuery.like(Content::getTitle, searchTitle);
        }
    }

    private ListContentDto getListContentDtoDraft(Integer pageNum, Integer pageSize, String userId, String searchTitle) {
        LambdaQueryWrapper<Content> listQuery = new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, ContentStatus.DRAFT)
                .eq(Content::getUserId, userId)
                .orderByDesc(Content::getCreateTime);
        bindSearchTitle(searchTitle, listQuery);

        PageHelper.startPage(pageNum, pageSize);
        List<Content> contents = contentMapper.selectList(listQuery);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        contentService.bindContentMember(contents);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }
}
