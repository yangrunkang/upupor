package com.upupor.service.service.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dao.mapper.CommentMapper;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.service.aggregation.service.CommentService;
import com.upupor.service.service.aggregation.service.MemberService;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.PageUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddCommentReq;
import com.upupor.spi.req.ListCommentReq;
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
        comment.setStatus(CcEnum.CommentStatus.NORMAL.getStatus());
        comment.setAgree(CcEnum.CommentAgree.NONE.getAgree());
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

        ListCommentDto listCommentDto = new ListCommentDto(pageInfo);
        listCommentDto.setCommentList(pageInfo.getList());
        listCommentDto.setPaginationHtml(PageUtils.paginationHtmlForComment(pageInfo.getTotal(), pageInfo.getPageNum(), listCommentReq.getPageSize()));

        return listCommentDto;
    }

    @Override
    public Integer total() {
        return commentMapper.total();
    }

    @Override
    public void bindCommentUserName(List<Comment> commentList) {
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
        listCommentReq.setStatus(CcEnum.CommentStatus.NORMAL.getStatus());
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        // 评论来源和文章类型一致
        listCommentReq.setCommentSource(content.getContentType());

        ListCommentDto listCommentDto = this.listComment(listCommentReq);
        List<Comment> comments = listCommentDto.getCommentList();
        if (!CollectionUtils.isEmpty(comments)) {
            this.bindCommentUserName(comments);
        }
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
        listCommentReq.setStatus(CcEnum.CommentStatus.NORMAL.getStatus());
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        ListCommentDto listCommentDto = this.listComment(listCommentReq);
        List<Comment> comments = listCommentDto.getCommentList();
        if (!CollectionUtils.isEmpty(comments)) {
            this.bindCommentUserName(comments);
        }
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
