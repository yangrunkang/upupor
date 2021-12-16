package com.upupor.web.controller;

import com.upupor.service.common.*;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.listener.event.ToCommentSuccessEvent;
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

        // 回复事件
        try {
            // 添加评论
            Comment comment = commentService.toComment(addCommentReq);

            // 评论的人(当前用户)
            Member currentUser = memberService.memberInfo(ServletUtils.getUserId());
            if (StringUtils.isEmpty(addCommentReq.getReplyToUserId())) {
                // 常规的评论
                String creatorUserId = getCreatorUserId(addCommentReq);
                comment(addCommentReq.getTargetId(), comment, creatorUserId);
            } else {
                // 处理回复某一条评论的逻辑
                Member beReplayedUser = memberService.memberInfo(addCommentReq.getReplyToUserId());
                if (Objects.nonNull(beReplayedUser)) {
                    ReplayCommentEvent replayCommentEvent = ReplayCommentEvent.builder()
                            .commentSource(comment.getCommentSource())
                            .targetId(comment.getTargetId())
                            .createReplayUserId(currentUser.getUserId())
                            .createReplayUserName(currentUser.getUserName())
                            .beRepliedUserId(beReplayedUser.getUserId())
                            .build();
                    eventPublisher.publishEvent(replayCommentEvent);
                }
            }
        } catch (Exception e) {
        }

        cc.setData(true);
        return cc;
    }

    /**
     * // 创作者的用户Id
     *
     * @param addCommentReq
     * @return
     */
    private String getCreatorUserId(AddCommentReq addCommentReq) {
        // 创作者的用户Id
        String creatorUserId = null;
        // 目前只有文章和短内容的评论
        if (CcEnum.CommentSource.contentSource().contains(addCommentReq.getCommentSource())) {
            Content content = contentService.getNormalContent(addCommentReq.getTargetId());
            creatorUserId = content.getUserId();
        } else if (addCommentReq.getCommentSource().equals(CcEnum.CommentSource.MESSAGE.getSource())) {
            creatorUserId = addCommentReq.getTargetId();
        } else if (addCommentReq.getCommentSource().equals(CcEnum.CommentSource.RADIO.getSource())) {
            Radio radio = radioService.getByRadioId(addCommentReq.getTargetId());
            creatorUserId = radio.getUserId();
        }

        if (Objects.isNull(creatorUserId)) {
            throw new BusinessException(ErrorCode.COMMENT_SOURCE_NULL);
        }
        return creatorUserId;
    }

    private void comment(String targetId, Comment comment, String belongUserId) {
        // 只有被评论的对象不是自己,才发消息
        if (belongUserId.equals(ServletUtils.getUserId())) {
            return;
        }

        // 添加消息
        ToCommentSuccessEvent event = ToCommentSuccessEvent.builder()
                .commenterUserId(comment.getUserId())
                .createTime(comment.getCreateTime())
                .commentId(comment.getCommentId())
                .commentSource(CcEnum.CommentSource.getBySource(comment.getCommentSource()))
                .targetId(targetId)
                .build();

        eventPublisher.publishEvent(event);
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
