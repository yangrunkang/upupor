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

package com.upupor.service.business.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.business.manage.service.CommentManageService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.mapper.CommentMapper;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.outer.req.ListCommentReq;
import com.upupor.service.utils.CcUtils;
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
