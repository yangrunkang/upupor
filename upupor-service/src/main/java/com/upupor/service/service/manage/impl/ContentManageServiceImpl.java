package com.upupor.service.service.manage.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.mapper.ContentMapper;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.manage.ContentManageService;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

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
        PageHelper.startPage(listContentReq.getPageNum(), listContentReq.getPageSize());
        List<Content> contents = contentMapper.listContentManage(listContentReq);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        contentService.bindContentMember(contents);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }

    private ListContentDto getListContentDtoDraft(Integer pageNum, Integer pageSize, String userId, String searchTitle) {
        PageHelper.startPage(pageNum, pageSize);
        List<Content> contents = contentMapper.listContentManageDraft(userId, searchTitle);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        contentService.bindContentMember(contents);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }
}
