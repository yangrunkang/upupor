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
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dao.entity.enhance.RadioEnhance;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.MessageType;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.MessageService;
import com.upupor.service.base.RadioService;
import com.upupor.service.business.build_msg.MessageBuilderInstance;
import com.upupor.service.business.build_msg.abstracts.BusinessMsgType;
import com.upupor.service.business.build_msg.abstracts.MsgType;
import com.upupor.service.business.build_msg.abstracts.dto.MemberProfileMsgParamDto;
import com.upupor.service.business.build_msg.abstracts.dto.RadioMsgParamDto;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.listener.event.ReplayCommentEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:50
 * @email: yangrunkang53@gmail.com
 */
@Component
public class RadioReply extends AbstractReplyComment<RadioEnhance> {
    @Resource
    private RadioService radioService;

    public RadioReply(MemberService memberService, MessageService messageService) {
        super(memberService, messageService);
    }

    @Override
    public Boolean isHandled(String targetId, ContentType contentType) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.RADIO.equals(contentType);
    }

    @Override
    public void reply(ReplayCommentEvent replayCommentEvent) {
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();
        String msgId = msgId();
        String beRepliedUserId = replayCommentEvent.getBeRepliedUserId();
        Member beReplayedUser = getMember(beRepliedUserId);

        Radio radio = getTarget(replayCommentEvent.getTargetId()).getRadio();


        RadioMsgParamDto radioMsgParamDto = RadioMsgParamDto.builder()
                .radioId(radio.getRadioId())
                .msgId(msgId)
                .radioIntro(radio.getRadioIntro())
                .build();
        String buildRadioMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.RADIO, radioMsgParamDto, MsgType.INNER_MSG);
        String buildRadioMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.RADIO, radioMsgParamDto, MsgType.EMAIL);

        MemberProfileMsgParamDto memberProfileMsgParamDto = MemberProfileMsgParamDto.builder()
                .memberUserId(creatorReplayUserId)
                .msgId(msgId)
                .memberUserName(creatorReplayUserName)
                .build();
        String buildProfileMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.EMAIL);


        String innerMsg = "电台《" + buildRadioMsg + "》,收到了来自" + buildProfileMsg + "的回复";
        String emailMsg = "电台《" + buildRadioMsgEmail + "》,收到了来自" + buildProfileMsg + "的回复";

        MessageSend.send(MessageModel.builder()
                .toUserId(beReplayedUser.getUserId())
                .emailModel(MessageModel.EmailModel.builder()
                        .title(title())
                        .content(emailMsg)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(innerMsg).build())
                .messageType(MessageType.USER_REPLAY)
                .messageId(msgId)
                .build());
    }


    @Override
    protected RadioEnhance getTarget(String targetId) {
        return radioService.getByRadioId(targetId);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        Radio radio = getTarget(targetId).getRadio();
        radio.setLatestCommentTime(CcDateUtil.getCurrentTime());
        radio.setLatestCommentUserId(commenterUserId);
        radioService.updateRadio(radio);
    }
}
