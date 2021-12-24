package com.upupor.service.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.mapper.CommentMapper;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.manage.service.CommentManageService;
import com.upupor.service.utils.CcUtils;
import com.upupor.spi.req.ListCommentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:20
 */
@Service
@RequiredArgsConstructor
public class CommentManageServiceImpl implements CommentManageService {

    private final CommentMapper commentMapper;


    @Override
    public ListCommentDto listComment(ListCommentReq listCommentReq) {
        Boolean allEmpty = CcUtils.isAllEmpty(listCommentReq.getUserId(), listCommentReq.getTargetId());
        if (allEmpty) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        PageHelper.startPage(listCommentReq.getPageNum(), listCommentReq.getPageSize());
        List<Comment> commentList = commentMapper.listManage(listCommentReq);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);

        ListCommentDto listCommentDto = new ListCommentDto(pageInfo);
        listCommentDto.setCommentList(pageInfo.getList());

        return listCommentDto;

    }
}
