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

package com.upupor.service.business.replay;

import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.data.service.MessageService;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.MessageType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.upupor.framework.CcConstant.MsgTemplate.*;

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
        Member beReplayedUser = getMember(replayCommentEvent.getBeRepliedUserId());
        String msgId = msgId();

        String innerMsg = buildMsgByTemplate(replayCommentEvent, msgId, MESSAGE_INTEGRAL);
        String emailMsg = buildMsgByTemplate(replayCommentEvent, msgId, MESSAGE_EMAIL);
        // 站内信通知
        getMessageService().addMessage(beReplayedUser.getUserId(), innerMsg, MessageType.USER_REPLAY, msgId);
        // 邮件通知
        String title = "留言板有新的回复消息";
        getMessageService().sendEmail(beReplayedUser.getEmail(), title, emailMsg, beReplayedUser.getUserId());
    }

    @NotNull
    private String buildMsgByTemplate(ReplayCommentEvent replayCommentEvent, String msgId, String template) {
        String msg;
        String targetId = replayCommentEvent.getTargetId();
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();
        // 留言板所有者(对应的就是事件的targetId)
        if (!targetId.equals(creatorReplayUserId)) {
            msg = String.format(template, targetId, msgId, "<strong>留言板</strong>")
                    + "收到了来自"
                    + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                    + "的回复,请" + String.format(template, targetId, msgId, "点击查看");
        } else {
            msg = String.format(template, creatorReplayUserId, msgId, "<strong>留言板</strong>")
                    + "收到了来自"
                    + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                    + "的回复,请" + String.format(template, creatorReplayUserId, msgId, "点击查看");
        }
        return msg;
    }

    @Override
    protected Member getTarget(String targetId) {
        return getMemberService().memberInfo(targetId);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {

    }
}
