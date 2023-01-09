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

package com.upupor.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.data.dao.entity.Comment;
import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.CommentEnhance;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dao.entity.enhance.RadioEnhance;
import com.upupor.data.dao.mapper.CommentMapper;
import com.upupor.data.dto.page.common.ListCommentDto;
import com.upupor.data.dto.query.ListCommentQuery;
import com.upupor.data.types.CommentAgree;
import com.upupor.data.types.CommentStatus;
import com.upupor.data.utils.PageUtils;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.CommentService;
import com.upupor.service.base.MemberService;
import com.upupor.service.outer.req.AddCommentReq;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.JwtUtils;
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
        comment.setUserId(JwtUtils.getUserId());
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
    public ListCommentDto listComment(ListCommentQuery query) {
        Boolean allEmpty = CcUtils.isAllEmpty(query.getUserId(), query.getTargetId());
        if (allEmpty && !query.getQueryAll()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Comment> commentList = commentMapper.list(query);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);

        List<CommentEnhance> commentEnhanceList = Converter.commentEnhance(commentList);
        bindCommentUser(commentEnhanceList);
        setCommentFloorNumber(commentEnhanceList, query.getPageNum(), query.getPageSize());

        ListCommentDto listCommentDto = new ListCommentDto(pageInfo);
        listCommentDto.setCommentEnhanceList(commentEnhanceList);
        // 评论特殊翻页,默认翻到最新一页,用户可以看到最新的评论
        listCommentDto.setPageDtoList(PageUtils.buildPageDtoListForComment(pageInfo.getTotal(), pageInfo.getPageNum(), query.getPageSize()));
        return listCommentDto;
    }

    private void setCommentFloorNumber(List<CommentEnhance> list, Integer pageNum, Integer pageSize) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            CommentEnhance commentEnhance = list.get(i);
            int toAdd = 0;
            if (pageNum > 1) {
                toAdd = (pageNum - 1) * pageSize;
            }
            commentEnhance.setFloorNum(String.valueOf((i + 1) + toAdd));
        }
    }


    @Override
    public void bindCommentUser(List<CommentEnhance> commentList) {
        if (CollectionUtils.isEmpty(commentList)) {
            return;
        }
        List<String> userIdList = commentList.stream()
                .map(CommentEnhance::getComment)
                .map(Comment::getUserId)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<MemberEnhance> memberEnhanceList = memberService.listByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(memberEnhanceList)) {
            return;
        }
        memberEnhanceList.forEach(memberEnhance -> commentList.forEach(comment -> {
            if (comment.getComment().getUserId().equals(memberEnhance.getMember().getUserId())) {
                comment.setMemberEnhance(memberEnhance);
            }
        }));
    }


    @Override
    public void bindContentComment(ContentEnhance contentEnhance, Integer pageNum, Integer pageSize) {
        Content content = contentEnhance.getContent();
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
        ListCommentQuery listCommentQuery = new ListCommentQuery();
        listCommentQuery.setTargetId(contentId);
        listCommentQuery.setStatus(CommentStatus.NORMAL);
        // 评论来源和文章类型一致
        listCommentQuery.setCommentSource(content.getContentType());
        listCommentQuery.setPageNum(pageNum);
        listCommentQuery.setPageSize(pageSize);

        ListCommentDto listCommentDto = this.listComment(listCommentQuery);
        contentEnhance.setListCommentDto(listCommentDto);
    }


    @Override
    public void bindRadioComment(RadioEnhance radioEnhance, Integer pageNum, Integer pageSize) {
        Radio radio = radioEnhance.getRadio();
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
        ListCommentQuery listCommentQuery = new ListCommentQuery();
        listCommentQuery.setTargetId(radioId);
        listCommentQuery.setStatus(CommentStatus.NORMAL);
        listCommentQuery.setPageNum(pageNum);
        listCommentQuery.setPageSize(pageSize);
        ListCommentDto listCommentDto = this.listComment(listCommentQuery);
        radioEnhance.setListCommentDto(listCommentDto);
    }

    @Override
    public Integer countByTargetId(String targetId) {
        if (StringUtils.isEmpty(targetId)) {
            return 0;
        }
        return commentMapper.countByTargetId(targetId);
    }
}
