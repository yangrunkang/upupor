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

package com.upupor.web.controller;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.data.dao.entity.Comment;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.service.CommentService;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.listener.event.ToCommentSuccessEvent;
import com.upupor.service.outer.req.AddCommentReq;
import com.upupor.service.outer.req.ListCommentReq;
import com.upupor.service.outer.req.UpdateCommentReq;
import com.upupor.service.types.CommentStatus;
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
 * ??????
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:50
 */
@Api(tags = "????????????")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @ApiOperation("????????????")
    @PostMapping("/add")
    @ResponseBody
    @UpuporLimit(limitType = LimitType.CREATE_COMMENT, needSpendMoney = true)
    public CcResponse add(AddCommentReq addCommentReq) {
        CcResponse cc = new CcResponse();
        if (Objects.isNull(addCommentReq.getCommentSource())) {
            throw new BusinessException(ErrorCode.COMMENT_SOURCE_NULL);
        }

        // ????????????????????? @ ??????,???????????????????????????????????????(??????????????????,??????????????????)
        if (!StringUtils.isEmpty(addCommentReq.getCommentContent()) && !addCommentReq.getCommentContent().contains(CcConstant.AT)) {
            addCommentReq.setReplyToUserId(null);
        }

        // ????????????
        try {
            // ????????????
            Comment comment = commentService.toComment(addCommentReq);

            // ????????????(????????????)
            Member currentUser = memberService.memberInfo(ServletUtils.getUserId());
            if (StringUtils.isEmpty(addCommentReq.getReplyToUserId())) {
                // ???????????????
                normalCommentEvent(addCommentReq.getTargetId(), comment);
            } else {
                // ????????????????????????????????????
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

    private void normalCommentEvent(String targetId, Comment comment) {
        // ????????????
        ToCommentSuccessEvent event = ToCommentSuccessEvent.builder()
                .commenterUserId(comment.getUserId())
                .createTime(comment.getCreateTime())
                .commentId(comment.getCommentId())
                .commentSource(comment.getCommentSource())
                .targetId(targetId)
                .build();

        eventPublisher.publishEvent(event);
    }


    @ApiOperation("????????????")
    @PostMapping("/edit")
    @ResponseBody
    public CcResponse edit(UpdateCommentReq updateCommentReq) {
        CcResponse cc = new CcResponse();

        Comment comment = commentService.getCommentByCommentId(updateCommentReq.getCommentId());
        // ???????????????????????????id?????????????????????
        ServletUtils.checkOperatePermission(comment.getUserId());

        comment.setStatus(updateCommentReq.getStatus());
        boolean update = commentService.update(comment);
        if (!update) {
            throw new BusinessException(ErrorCode.EDIT_COMMENT_FAILURE);
        }
        cc.setData(true);
        return cc;
    }


    @ApiOperation("??????????????????")
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
        // ?????????
        listCommentReq.setStatus(CommentStatus.NORMAL);
        cc.setData(commentService.listComment(listCommentReq));
        return cc;
    }

}
