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
import com.upupor.service.types.MessageType;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.MESSAGE_INTEGRAL;
import static com.upupor.service.common.CcConstant.MsgTemplate.PROFILE_INNER_MSG;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:50
 * @email: yangrunkang53@gmail.com
 */
@Component
public class MessageBoardReply extends AbstractReplyComment<Member> {

    public MessageBoardReply(MemberService memberService, MessageService messageService) {
        super(memberService, messageService);
    }

    @Override
    public Boolean isHandled(String targetId, ContentType contentType) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.MESSAGE.equals(contentType);
    }

    @Override
    public void reply(ReplayCommentEvent replayCommentEvent) {
        String title = "留言板有新的回复消息";
        String msgId = msgId();
        String targetId = replayCommentEvent.getTargetId();
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();

        Member beReplayedUser = getMember(replayCommentEvent.getBeRepliedUserId());

        String msg = null;

        // 留言板所有者(对应的就是事件的targetId)
        if (!targetId.equals(creatorReplayUserId)) {
            msg = String.format(MESSAGE_INTEGRAL, targetId, msgId, "<strong>留言板</strong>")
                    + "收到了来自"
                    + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                    + "的回复,请" + String.format(MESSAGE_INTEGRAL, targetId, msgId, "点击查看");
        } else {
            msg = String.format(MESSAGE_INTEGRAL, creatorReplayUserId, msgId, "<strong>留言板</strong>")
                    + "收到了来自"
                    + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                    + "的回复,请" + String.format(MESSAGE_INTEGRAL, creatorReplayUserId, msgId, "点击查看");
        }

        // 站内信通知
        getMessageService().addMessage(beReplayedUser.getUserId(), msg, MessageType.USER_REPLAY, msgId);

        // 邮件通知
        getMessageService().sendEmail(beReplayedUser.getEmail(), title, msg, beReplayedUser.getUserId());
    }

    @Override
    protected Member getTarget(String targetId) {
        return getMemberService().memberInfo(targetId);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {

    }
}
