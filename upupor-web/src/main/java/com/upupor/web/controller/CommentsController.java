package com.upupor.web.controller;

import com.upupor.service.common.*;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.listener.event.CommentMessageEvent;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.service.CommentService;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.RadioService;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddCommentReq;
import com.upupor.spi.req.ListCommentReq;
import com.upupor.spi.req.UpdateCommentReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


/**
 * 评论
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:50
 */
@Api(tags = "评论服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentsController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final ContentService contentService;
    private final RadioService radioService;
    private final ApplicationEventPublisher eventPublisher;

    @ApiOperation("添加评论")
    @PostMapping("/add")
    @ResponseBody
    public CcResponse add(AddCommentReq addCommentReq) {
        CcResponse cc = new CcResponse();
        if (Objects.isNull(addCommentReq.getCommentSource())) {
            throw new BusinessException(ErrorCode.COMMENT_SOURCE_NULL);
        }

        // 检查内容有没有 @ 符号,如果没有说明没有回复任何人(前端有做控制,后端再控制下)
        if (!StringUtils.isEmpty(addCommentReq.getCommentContent()) && !addCommentReq.getCommentContent().contains(CcConstant.AT)) {
            addCommentReq.setReplyToUserId(null);
        }

        // 获取当前登录的用户Id
        String userId = ServletUtils.getUserId();

        // 评论的人
        Member addCommentMember = memberService.memberInfo(userId);

        Boolean isContentTable = false;
        Boolean isRadioTable = false;

        // 被评论对象的所属者
        String belongUserId = null;
        // 目前只有文章和短内容的评论
        if (addCommentReq.getCommentSource().equals(CcEnum.CommentSource.SHORT_CONTENT.getSource())) {
            Content content = contentService.getNormalContent(addCommentReq.getTargetId());
            if (Objects.isNull(content)) {
                throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
            }

            belongUserId = content.getUserId();
            isContentTable = true;
        } else if (addCommentReq.getCommentSource() >= CcEnum.CommentSource.TECH.getSource() && addCommentReq.getCommentSource() <= CcEnum.CommentSource.RECORD.getSource()) {
            Content content = contentService.getNormalContent(addCommentReq.getTargetId());
            if (Objects.isNull(content)) {
                throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
            }
            belongUserId = content.getUserId();
            isContentTable = true;
        } else if (addCommentReq.getCommentSource().equals(CcEnum.CommentSource.MESSAGE.getSource())) {
            belongUserId = addCommentReq.getTargetId();
        } else if (addCommentReq.getCommentSource().equals(CcEnum.CommentSource.RADIO.getSource())) {
            Radio radio = radioService.getByRadioId(addCommentReq.getTargetId());
            if (Objects.isNull(radio)) {
                throw new BusinessException(ErrorCode.RADIO_NOT_EXISTS);
            }
            belongUserId = radio.getUserId();
            isRadioTable = true;
        }

        if (Objects.isNull(belongUserId)) {
            throw new BusinessException(ErrorCode.COMMENT_SOURCE_NULL);
        }

        // 回复事件
        try {
            // 添加评论
            Comment comment = commentService.addComment(addCommentReq);
            if (isContentTable) {
                Content content = contentService.getNormalContent(addCommentReq.getTargetId());
                if (Objects.isNull(content)) {
                    throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
                }

                content.setLatestCommentTime(comment.getCreateTime());
                content.setLatestCommentUserId(comment.getUserId());
                contentService.updateContent(content);
            }

            if (isRadioTable) {
                Radio radio = radioService.getByRadioId(addCommentReq.getTargetId());
                if (Objects.isNull(radio)) {
                    throw new BusinessException(ErrorCode.RADIO_NOT_EXISTS);
                }
                radio.setLatestCommentTime(comment.getCreateTime());
                radio.setLatestCommentUserId(comment.getUserId());
                radioService.updateRadio(radio);
            }


            if (!StringUtils.isEmpty(addCommentReq.getReplyToUserId())) {
                Member beReplayedUser = memberService.memberInfo(addCommentReq.getReplyToUserId());
                if (Objects.nonNull(beReplayedUser)) {
                    ReplayCommentEvent replayCommentEvent = new ReplayCommentEvent();
                    replayCommentEvent.setAddCommentMember(addCommentMember);
                    replayCommentEvent.setAddCommentReq(addCommentReq);
                    replayCommentEvent.setComment(comment);
                    eventPublisher.publishEvent(replayCommentEvent);
                } else {
                    // 评论
                    comment(addCommentReq, comment, addCommentMember, belongUserId);
                }
            } else {
                // 评论
                comment(addCommentReq, comment, addCommentMember, belongUserId);
            }
        } catch (Exception e) {
        }

        cc.setData(true);
        return cc;
    }

    private void comment(AddCommentReq addCommentReq, Comment comment, Member addCommentMember, String belongUserId) {
        // 只有被评论的对象不是自己,才发消息
        if (!belongUserId.equals(ServletUtils.getUserId())) {
            // 添加消息
            CommentMessageEvent commentMessageEvent = new CommentMessageEvent();
            commentMessageEvent.setAddCommentMember(addCommentMember);
            commentMessageEvent.setAddCommentReq(addCommentReq);
            commentMessageEvent.setComment(comment);
            eventPublisher.publishEvent(commentMessageEvent);
        }
    }


    @ApiOperation("编辑评论")
    @PostMapping("/edit")
    @ResponseBody
    public CcResponse edit(UpdateCommentReq updateCommentReq) {
        CcResponse cc = new CcResponse();

        Comment comment = commentService.getCommentByCommentId(updateCommentReq.getCommentId());
        // 校验内容所属的用户id是否是当前用户
        ServletUtils.checkOperatePermission(comment.getUserId());

        comment.setStatus(updateCommentReq.getStatus());
        boolean update = commentService.update(comment);
        if (!update) {
            throw new BusinessException(ErrorCode.EDIT_COMMENT_FAILURE);
        }
        cc.setData(true);
        return cc;
    }


    @ApiOperation("获取评论列表")
    @PostMapping("/list")
    @ResponseBody
    public CcResponse list(ListCommentReq listCommentReq) {
        CcResponse cc = new CcResponse();
        if (StringUtils.isEmpty(listCommentReq.getPageNum())) {
            listCommentReq.setPageNum(CcConstant.Page.NUM);
        }

        if (StringUtils.isEmpty(listCommentReq.getPageSize())) {
            listCommentReq.setPageSize(CcConstant.Page.SIZE);
        }
        // 正常的
        listCommentReq.setStatus(CcEnum.CommentStatus.NORMAL.getStatus());
        cc.setData(commentService.listComment(listCommentReq));
        return cc;
    }

}
