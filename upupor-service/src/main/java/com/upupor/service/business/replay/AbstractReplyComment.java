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

package com.upupor.service.business.replay;

import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.types.ContentType;
import com.upupor.service.utils.CcUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:47
 * @email: yangrunkang53@gmail.com
 */
@RequiredArgsConstructor
public abstract class AbstractReplyComment<T> {
    @Getter
    private final MemberService memberService;
    @Getter
    private final MessageService messageService;
    /**
     * 是否需要处理
     * @param targetId
     * @param contentType
     * @return
     */
    public abstract Boolean isHandled(String targetId, ContentType contentType);

    /**
     * 回复
     * @param replayCommentEvent
     */
    public abstract void reply(ReplayCommentEvent replayCommentEvent);

    /**
     * 更新目标的评论者信息
     * @param targetId
     * @param commenterUserId
     */
    public abstract void updateTargetCommentCreatorInfo(String targetId,String commenterUserId);


    /**
     * 获取目标对象
     * @param targetId
     * @return
     */
    protected abstract T getTarget(String targetId);

    protected String msgId(){
        return CcUtils.getUuId();
    }

    protected Member getMember(String userId){
        return memberService.memberInfo(userId);
    }

    protected String title(){
        return "您的评论收到了新的回复";
    }

}
