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

package com.upupor.service.business.comment.comment;

import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dao.entity.enhance.RadioEnhance;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.MessageType;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.RadioService;
import com.upupor.service.business.comment.comment.abstracts.AbstractComment;
import com.upupor.service.business.links.LinkBuilderInstance;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.MsgType;
import com.upupor.service.business.links.abstracts.dto.MemberProfileLinkParamDto;
import com.upupor.service.business.links.abstracts.dto.RadioLinkParamDto;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:21
 * @email: yangrunkang53@gmail.com
 */
@Component
public class RadioComment extends AbstractComment<RadioEnhance> {
    @Resource
    private RadioService radioService;

    public RadioComment(MemberService memberService) {
        super(memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId, Long floorNum) {
        String msgId = getMsgId();

        Radio radio = radioService.getByRadioId(targetId).getRadio();
        String radioName = radio.getRadioIntro();

        Member radioAuthor = getMemberInfo(radio.getUserId()).getMember();
        String radioAuthorUserId = radioAuthor.getUserId();

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId).getMember();
        String commenterUserName = commenter.getUserName();

        // 如果作者自己评论自己就不用发邮件了
        if (commenter.getUserId().equals(radio.getUserId())) {
            return;
        }

        RadioLinkParamDto radioLinkParamDto = RadioLinkParamDto.builder()
                .radioId(radio.getRadioId())
                .msgId(msgId)
                .floorNum(floorNum)
                .radioIntro(radioName)
                .build();
        String buildRadioMsg = LinkBuilderInstance.buildLink(BusinessLinkType.RADIO, radioLinkParamDto, MsgType.INNER_MSG);
        String buildRadioMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.RADIO, radioLinkParamDto, MsgType.EMAIL);

        MemberProfileLinkParamDto memberProfileLinkParamDto = MemberProfileLinkParamDto.builder()
                .memberUserId(commenterUserId)
                .msgId(msgId)
                .floorNum(floorNum)
                .memberUserName(commenterUserName)
                .build();
        String buildProfileMsg = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.EMAIL);

        // 站内信通知对方收到新的留言
        String msg = "您收到了新的电台评论,点击<strong>《" + buildRadioMsg + "》</strong>查看,评论来自" + buildProfileMsg;
        // 发送邮件通知对方收到新的留言
        String emailContent = "点击" + buildRadioMsgEmail + ",评论来自 " + buildProfileMsgEmail;

        MessageSend.send(MessageModel.builder()
                .toUserId(radioAuthorUserId)
                .messageType(MessageType.USER_REPLAY)
                .emailModel(MessageModel.EmailModel.builder()
                        .title("您有新的电台评论,快去看看吧")
                        .content(emailContent)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(msg).build())
                .messageId(msgId)
                .build());
    }

    @Override
    protected RadioEnhance getTarget(String targetId) {
        return radioService.getByRadioId(targetId);
    }

    @Override
    public Boolean confirmSource(ContentType contentType, String targetId) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.RADIO.equals(contentType);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        Radio radio = getTarget(targetId).getRadio();
        radio.setLatestCommentTime(CcDateUtil.getCurrentTime());
        radio.setLatestCommentUserId(commenterUserId);
        radioService.updateRadio(radio);
    }
}
