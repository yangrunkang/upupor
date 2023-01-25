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
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.listener.event.ReplayCommentEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.upupor.framework.CcConstant.MsgTemplate.*;

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
        String innerMsg = buildByTemplate(creatorReplayUserId, creatorReplayUserName, msgId, radio, RADIO_INNER_MSG);
        String emailMsg = buildByTemplate(creatorReplayUserId, creatorReplayUserName, msgId, radio, RADIO_EMAIL);

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

    @NotNull
    private String buildByTemplate(String creatorReplayUserId, String creatorReplayUserName, String msgId, Radio radio, String template) {
        return "电台《" + String.format(template, radio.getRadioId(), msgId, radio.getRadioIntro())
                + "》,收到了来自"
                + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                + "的回复,请" + String.format(template, radio.getRadioId(), msgId, "点击查看");
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
