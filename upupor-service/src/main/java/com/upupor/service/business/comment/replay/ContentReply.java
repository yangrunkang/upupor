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

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.MessageType;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.MessageService;
import com.upupor.service.business.links.LinkBuilderInstance;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.MsgType;
import com.upupor.service.business.links.abstracts.dto.ContentLinkParamDto;
import com.upupor.service.business.links.abstracts.dto.MemberProfileLinkParamDto;
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
public class ContentReply extends AbstractReplyComment<ContentEnhance> {
    @Resource
    private ContentService contentService;

    public ContentReply(MemberService memberService, MessageService messageService) {
        super(memberService, messageService);
    }


    @Override
    public Boolean isHandled(String targetId, ContentType contentType) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.contentSource().contains(contentType);
    }

    @Override
    public void reply(ReplayCommentEvent replayCommentEvent) {
        String msgId = msgId();
        String beRepliedUserId = replayCommentEvent.getBeRepliedUserId();
        Member beRepliedMember = getMember(beRepliedUserId);
        String createReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String createReplayUserName = replayCommentEvent.getCreateReplayUserName();

        Content content = getTarget(replayCommentEvent.getTargetId()).getContent();

        ContentLinkParamDto contentLinkParamDto = ContentLinkParamDto.builder()
                .contentId(content.getContentId())
                .msgId(msgId)
                .contentTitle(content.getTitle())
                .build();
        String buildContentMsg = LinkBuilderInstance.buildLink(BusinessLinkType.CONTENT, contentLinkParamDto, MsgType.INNER_MSG);
        String buildContentMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.CONTENT, contentLinkParamDto, MsgType.EMAIL);

        MemberProfileLinkParamDto memberProfileLinkParamDto = MemberProfileLinkParamDto.builder()
                .memberUserId(createReplayUserId)
                .msgId(msgId)
                .memberUserName(createReplayUserName)
                .build();
        String buildProfileMsg = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.EMAIL);

        // 评论回复站内信
        String innerMsg = "您关于《" + buildContentMsg + "》的文章评论,收到了来自" + buildProfileMsg + "的回复";
        ;
        String emailMsg = "您关于《" + buildContentMsgEmail + "》的文章评论,收到了来自" + buildProfileMsgEmail + "的回复";

        MessageSend.send(MessageModel.builder()
                .toUserId(beRepliedUserId)
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
    protected ContentEnhance getTarget(String targetId) {
        return contentService.getNormalContent(targetId);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        ContentEnhance contentEnhance = getTarget(targetId);
        Content content = contentEnhance.getContent();
        content.setLatestCommentTime(CcDateUtil.getCurrentTime());
        content.setLatestCommentUserId(commenterUserId);
        contentService.updateContent(contentEnhance);
    }
}
