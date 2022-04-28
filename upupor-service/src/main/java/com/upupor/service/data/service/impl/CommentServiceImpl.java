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

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.data.dao.entity.Comment;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Radio;
import com.upupor.service.data.dao.mapper.CommentMapper;
import com.upupor.service.data.service.CommentService;
import com.upupor.service.data.service.MemberService;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.outer.req.AddCommentReq;
import com.upupor.service.outer.req.ListCommentReq;
import com.upupor.service.types.CommentAgree;
import com.upupor.service.types.CommentStatus;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.PageUtils;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:47
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final MemberService memberService;

    @Override
    public Comment toComment(AddCommentReq addCommentReq) {

        Comment comment = new Comment();
        BeanUtils.copyProperties(addCommentReq, comment);
        comment.setUserId(ServletUtils.getUserId());
        comment.setCommentId(CcUtils.getUuId());
        comment.setStatus(CommentStatus.NORMAL);
        comment.setAgree(CommentAgree.NONE);
        comment.setLikeNum(BigDecimal.ZERO.intValue());
        comment.setCreateTime(CcDateUtil.getCurrentTime());
        comment.setSysUpdateTime(new Date());

        if (commentMapper.insert(comment) > 0) {
            return comment;
        }
        return null;
    }

    @Override
    public Boolean update(Comment comment) {
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public Comment getCommentByCommentId(String commentId) {
        LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getCommentId, commentId);
        Comment comment = commentMapper.selectOne(query);
        Asserts.notNull(comment, ErrorCode.COMMENT_NOT_EXISTS);
        return comment;
    }

    @Override
    public ListCommentDto listComment(ListCommentReq listCommentReq) {
        Boolean allEmpty = CcUtils.isAllEmpty(listCommentReq.getUserId(), listCommentReq.getTargetId());
        if (allEmpty) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        PageHelper.startPage(listCommentReq.getPageNum(), listCommentReq.getPageSize());
        List<Comment> commentList = commentMapper.list(listCommentReq);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);

        bindCommentUser(pageInfo.getList());

        setCommentFloorNumber(pageInfo.getList(),listCommentReq.getPageNum(),listCommentReq.getPageSize());

        ListCommentDto listCommentDto = new ListCommentDto(pageInfo);
        listCommentDto.setCommentList(pageInfo.getList());
        listCommentDto.setPaginationHtml(PageUtils.paginationHtmlForComment(pageInfo.getTotal(), pageInfo.getPageNum(), listCommentReq.getPageSize()));

        return listCommentDto;
    }

    private void setCommentFloorNumber(List<Comment> list,Integer pageNum,Integer pageSize) {
        if(CollectionUtils.isEmpty(list)) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            Comment comment = list.get(i);
            int toAdd = 0;
            if(pageNum>1){
                toAdd = (pageNum - 1) * pageSize;
            }
            comment.setFloorNum(String.valueOf((i+1)+toAdd));
        }
    }


    @Override
    public void bindCommentUser(List<Comment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) {
            return;
        }
        List<String> userIdList = commentList.stream().map(Comment::getUserId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<Member> memberList = memberService.listByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        memberList.forEach(member -> {
            commentList.forEach(comment -> {
                if (comment.getUserId().equals(member.getUserId())) {
                    comment.setMember(member);
                }
            });
        });
    }


    @Override
    public void bindContentComment(Content content, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(content) || StringUtils.isEmpty(content.getContentId())) {
            return;
        }

        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        String contentId = content.getContentId();
        if (StringUtils.isEmpty(contentId)) {
            return;
        }
        ListCommentReq listCommentReq = new ListCommentReq();
        listCommentReq.setTargetId(contentId);
        listCommentReq.setStatus(CommentStatus.NORMAL);
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        // 评论来源和文章类型一致
        listCommentReq.setCommentSource(content.getContentType());

        ListCommentDto listCommentDto = this.listComment(listCommentReq);
        content.setListCommentDto(listCommentDto);
    }


    @Override
    public void bindRadioComment(Radio radio, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(radio) || StringUtils.isEmpty(radio.getRadioId())) {
            return;
        }

        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        String radioId = radio.getRadioId();
        if (StringUtils.isEmpty(radioId)) {
            return;
        }
        ListCommentReq listCommentReq = new ListCommentReq();
        listCommentReq.setTargetId(radioId);
        listCommentReq.setStatus(CommentStatus.NORMAL);
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        ListCommentDto listCommentDto = this.listComment(listCommentReq);
        radio.setListCommentDto(listCommentDto);
    }

    @Override
    public Integer countByTargetId(String targetId) {
        if (StringUtils.isEmpty(targetId)) {
            return 0;
        }
        return commentMapper.countByTargetId(targetId);
    }
}
