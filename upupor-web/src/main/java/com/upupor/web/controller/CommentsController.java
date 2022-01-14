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

package com.upupor.web.controller;

import com.upupor.service.business.aggregation.service.CommentService;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.RadioService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcResponse;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.business.aggregation.dao.entity.Comment;
import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.dao.entity.Radio;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.listener.event.ToCommentSuccessEvent;
import com.upupor.service.outer.req.AddCommentReq;
import com.upupor.service.outer.req.ListCommentReq;
import com.upupor.service.outer.req.UpdateCommentReq;
import com.upupor.service.types.CommentStatus;
import com.upupor.service.types.ContentType;
import com.upupor.service.utils.ServletUtils;
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
                normalCommentEvent(addCommentReq.getTargetId(), comment, creatorUserId);
            } else {
                // 处理回复某一条评论的逻辑
                replayCommentEvent(addCommentReq, comment, currentUser);
            }
        } catch (Exception ignored) {
        }

        cc.setData(true);
        return cc;
    }

    private void replayCommentEvent(AddCommentReq addCommentReq, Comment comment, Member currentUser) {
        if (Objects.nonNull(addCommentReq.getReplyToUserId())) {
            ReplayCommentEvent replayCommentEvent = ReplayCommentEvent.builder()
                    .commentSource(comment.getCommentSource())
                    .targetId(comment.getTargetId())
                    .createReplayUserId(currentUser.getUserId())
                    .createReplayUserName(currentUser.getUserName())
                    .beRepliedUserId(addCommentReq.getReplyToUserId())
                    .build();
            eventPublisher.publishEvent(replayCommentEvent);
        }
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
        if (ContentType.contentSource().contains(addCommentReq.getCommentSource())) {
            Content content = contentService.getNormalContent(addCommentReq.getTargetId());
            creatorUserId = content.getUserId();
        } else if (ContentType.MESSAGE.equals(addCommentReq.getCommentSource())) {
            creatorUserId = addCommentReq.getTargetId();
        } else if (ContentType.RADIO.equals(addCommentReq.getCommentSource())) {
            Radio radio = radioService.getByRadioId(addCommentReq.getTargetId());
            creatorUserId = radio.getUserId();
        }

        if (Objects.isNull(creatorUserId)) {
            throw new BusinessException(ErrorCode.COMMENT_SOURCE_NULL);
        }
        return creatorUserId;
    }

    private void normalCommentEvent(String targetId, Comment comment, String belongUserId) {
        // 只有被评论的对象不是自己,才发消息
        if (belongUserId.equals(ServletUtils.getUserId())) {
            return;
        }

        // 添加消息
        ToCommentSuccessEvent event = ToCommentSuccessEvent.builder()
                .commenterUserId(comment.getUserId())
                .createTime(comment.getCreateTime())
                .commentId(comment.getCommentId())
                .commentSource(comment.getCommentSource())
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
        listCommentReq.setStatus(CommentStatus.NORMAL);
        cc.setData(commentService.listComment(listCommentReq));
        return cc;
    }

}
