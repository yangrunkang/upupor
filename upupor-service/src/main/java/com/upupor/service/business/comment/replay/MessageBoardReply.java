/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2023 yangrunkang
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

package com.upupor.service.business.comment.replay;

import com.upupor.data.dao.entity.Member;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.MessageType;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.MessageService;
import com.upupor.service.business.build_msg.MessageBuilderInstance;
import com.upupor.service.business.build_msg.abstracts.BusinessMsgType;
import com.upupor.service.business.build_msg.abstracts.MsgType;
import com.upupor.service.business.build_msg.abstracts.dto.MemberProfileMsgParamDto;
import com.upupor.service.business.build_msg.abstracts.dto.MessageBoardMsgParamDto;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.listener.event.ReplayCommentEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
        String targetId = replayCommentEvent.getTargetId();
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();

        Member targetMember = getMember(targetId);

        MemberProfileMsgParamDto memberProfileMsgParamDto = MemberProfileMsgParamDto.builder()
                .memberUserId(creatorReplayUserId)
                .msgId(msgId)
                .memberUserName(creatorReplayUserName)
                .build();
        String buildProfileMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.EMAIL);

        String emailMsg, innerMsg;
        // 留言板所有者(对应的就是事件的targetId)
        if (!targetId.equals(creatorReplayUserId)) {
            MessageBoardMsgParamDto messageBoardMsgParamDto = MessageBoardMsgParamDto.builder()
                    .targetUserId(targetId)
                    .msgId(msgId)
                    .title("<strong>我的留言板</strong>")
                    .build();
            String buildMessageBoardMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.MESSAGE_BOARD, messageBoardMsgParamDto, MsgType.INNER_MSG);
            String buildMessageBoardMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.MESSAGE_BOARD, messageBoardMsgParamDto, MsgType.EMAIL);


            emailMsg = buildMessageBoardMsgEmail + "收到了来自" + buildProfileMsgEmail + "的回复";
            innerMsg = buildMessageBoardMsg + "收到了来自" + buildProfileMsg + "的回复";
        } else {
            MessageBoardMsgParamDto messageBoardMsgParamDto = MessageBoardMsgParamDto.builder()
                    .targetUserId(creatorReplayUserId)
                    .msgId(msgId)
                    .title("<strong>我在" + targetMember.getUserName() + "留言板上的留言内容</strong>")
                    .build();
            String buildMessageBoardMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.MESSAGE_BOARD, messageBoardMsgParamDto, MsgType.INNER_MSG);
            String buildMessageBoardMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.MESSAGE_BOARD, messageBoardMsgParamDto, MsgType.EMAIL);


            emailMsg = buildMessageBoardMsgEmail + "收到了来自" + buildProfileMsgEmail + "的回复";
            innerMsg = buildMessageBoardMsg + "收到了来自" + buildProfileMsg + "的回复";
        }

        MessageSend.send(MessageModel.builder()
                .toUserId(beReplayedUser.getUserId())
                .messageType(MessageType.USER_REPLAY)
                .emailModel(MessageModel.EmailModel.builder()
                        .title(title())
                        .content(emailMsg)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(innerMsg).build())
                .messageId(msgId)
                .build());
    }

    @Override
    protected Member getTarget(String targetId) {
        return getMemberService().memberInfo(targetId).getMember();
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {

    }
}
