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

package com.upupor.web.api;

import com.upupor.data.dao.entity.Comment;
import com.upupor.data.dao.entity.Member;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.base.CommentService;
import com.upupor.service.base.MemberService;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.listener.event.ToCommentSuccessEvent;
import com.upupor.service.outer.req.AddCommentReq;
import com.upupor.service.outer.req.UpdateCommentReq;
import com.upupor.service.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @ApiOperation("添加评论")
    @PostMapping("/add")
    @UpuporLimit(limitType = LimitType.CREATE_COMMENT, needSpendMoney = true)
    @Upgrade2ApiSuccess
    public CcResponse add(@RequestBody AddCommentReq addCommentReq) {
        CcResponse cc = new CcResponse();
        if (Objects.isNull(addCommentReq.getCommentSource())) {
            throw new BusinessException(ErrorCode.COMMENT_SOURCE_NULL);
        }

        // 回复事件
        try {
            // 添加评论
            Comment comment = commentService.create(addCommentReq);
            // 评论的人(当前用户)
            Member currentUser = memberService.memberInfo(JwtUtils.getUserId()).getMember();
            if (StringUtils.isEmpty(addCommentReq.getReplyToUserId())) {
                // 常规的评论
                normalCommentEvent(addCommentReq.getTargetId(), comment);
            } else {
                // 处理回复某一条评论的逻辑
                replayCommentEvent(addCommentReq, comment, currentUser);
            }
        } catch (Exception ignored) {
            throw new BusinessException(ErrorCode.COMMENT_FAILED);
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

    private void normalCommentEvent(String targetId, Comment comment) {
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
    @Upgrade2ApiSuccess
    public CcResponse edit(@RequestBody UpdateCommentReq updateCommentReq) {
        CcResponse cc = new CcResponse();

        Comment comment = commentService.getCommentByCommentId(updateCommentReq.getCommentId());
        // 校验内容所属的用户id是否是当前用户
        JwtUtils.checkOperatePermission(comment.getUserId());

        comment.setStatus(updateCommentReq.getStatus());
        boolean update = commentService.update(comment);
        if (!update) {
            throw new BusinessException(ErrorCode.EDIT_COMMENT_FAILURE);
        }
        cc.setData(true);
        return cc;
    }


}
