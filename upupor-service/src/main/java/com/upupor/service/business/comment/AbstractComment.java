/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.service.business.comment;

import com.upupor.service.business.aggregation.service.CommentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.utils.CcUtils;

/**
 * 评论抽象
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:15
 */
public abstract class AbstractComment<T> {
    private final CommentService commentService;
    private final MemberService memberService;

    public AbstractComment(CommentService commentService, MemberService memberService) {
        this.commentService = commentService;
        this.memberService = memberService;
    }

    /**
     * 评论
     *  @param targetId        评论目标
     * @param commenterUserId 评论者id
     * @param commentId       评论记录的id
     */
    public abstract void comment(String targetId, String commenterUserId, String commentId);

    /**
     * 获取目标
     *
     * @param targetId 评论目标
     * @return 返回目标
     */
    protected abstract T getTarget(String targetId);

    /**
     * @param commentSource 评论来源
     * @param targetId
     * @return 判断是否处理
     */
    public abstract Boolean confirmSource(CcEnum.CommentSource commentSource,String targetId);

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    protected Member getMemberInfo(String userId) {
        return memberService.memberInfo(userId);
    }

    /**
     * @return 消息Id
     */
    protected String getMsgId() {
        return CcUtils.getUuId();
    }

    /**
     * 更新目标的评论者信息
     * @param targetId
     * @param commenterUserId
     */
    public abstract void updateTargetCommentCreatorInfo(String targetId,String commenterUserId);

}
