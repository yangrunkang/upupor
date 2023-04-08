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

package com.upupor.service.listener;

import com.upupor.service.business.comment.comment.abstracts.AbstractComment;
import com.upupor.service.business.comment.replay.AbstractReplyComment;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.listener.event.ToCommentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * upupor 网站站内信
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:21
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CommentListener {

    private final List<AbstractComment<?>> abstractCommentList;
    private final List<AbstractReplyComment<?>> abstractReplyCommentList;

    /**
     * 添加评论成功
     *
     * @param event
     */
    @EventListener
    @Async
    public void toCommentSuccessEvent(ToCommentSuccessEvent event) {
        for (AbstractComment<?> abstractComment : abstractCommentList) {
            try {
                if (abstractComment.confirmSource(event.getCommentSource(), event.getTargetId())) {
                    // 评论
                    abstractComment.comment(event.getTargetId(), event.getCommenterUserId(), event.getFloorNum());
                    // 记录评论人和评论时间
                    abstractComment.updateTargetCommentCreatorInfo(event.getTargetId(), event.getCommenterUserId());
                }
            } catch (Exception e) {
            }
        }
    }


    /**
     * 回复评论事件
     *
     * @param replayCommentEvent
     */
    @EventListener
    @Async
    public void replayCommentEvent(ReplayCommentEvent replayCommentEvent) {
        String beRepliedUserId = replayCommentEvent.getBeRepliedUserId();
        String createReplayUserId = replayCommentEvent.getCreateReplayUserId();

        // 如果自己回复自己则啥也不做
        if (beRepliedUserId.equals(createReplayUserId)) {
            return;
        }

        for (AbstractReplyComment<?> abstractReplyComment : abstractReplyCommentList) {
            try {
                if (abstractReplyComment.isHandled(replayCommentEvent.getTargetId(), replayCommentEvent.getCommentSource())) {
                    abstractReplyComment.reply(replayCommentEvent);
                    abstractReplyComment.updateTargetCommentCreatorInfo(replayCommentEvent.getTargetId(), createReplayUserId);
                }
            } catch (Exception e) {
                log.error("回复评论异常,{}", e);
            }
        }
    }


}
