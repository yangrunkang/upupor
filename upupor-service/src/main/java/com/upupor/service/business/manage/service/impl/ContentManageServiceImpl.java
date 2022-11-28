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

package com.upupor.service.business.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.mapper.ContentMapper;
import com.upupor.data.dto.page.common.ListContentDto;
import com.upupor.data.types.ContentStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.base.ContentService;
import com.upupor.service.business.manage.service.ContentManageService;
import com.upupor.service.outer.req.ListContentReq;
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

    private ListContentDto getListContentDto(ListContentReq listContentReq) {

        LambdaQueryWrapper<Content> listQuery = new LambdaQueryWrapper<Content>()
                .notIn(Content::getStatus, ContentStatus.notIn())
                .eq(StringUtils.isNotEmpty(listContentReq.getUserId()), Content::getUserId, listContentReq.getUserId())
                .eq(StringUtils.isNotEmpty(listContentReq.getContentId()), Content::getContentId, listContentReq.getContentId())
                .eq(Objects.nonNull(listContentReq.getSelect()) && "ONLY_SELF_SEE".equals(listContentReq.getSelect()), Content::getStatus, ContentStatus.ONLY_SELF_CAN_SEE)
                .like(StringUtils.isNotEmpty(listContentReq.getSearchTitle()), Content::getTitle, listContentReq.getSearchTitle())
                .orderByDesc(Content::getCreateTime);

        PageHelper.startPage(listContentReq.getPageNum(), listContentReq.getPageSize());
        List<Content> contents = contentMapper.selectList(listQuery);
        List<ContentEnhance> contentEnhances = Converter.contentEnhance(contents);
        PageInfo<ContentEnhance> pageInfo = new PageInfo<>(contentEnhances);

        contentService.bindContentMember(contentEnhances);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }

}
