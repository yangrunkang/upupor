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

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.MessageType;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.MemberIntegralService;
import com.upupor.service.base.MemberService;
import com.upupor.service.business.comment.comment.abstracts.AbstractComment;
import com.upupor.service.business.links.LinkBuilderInstance;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.MsgType;
import com.upupor.service.business.links.abstracts.dto.ContentLinkParamDto;
import com.upupor.service.business.links.abstracts.dto.MemberProfileLinkParamDto;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:19
 * @email: yangrunkang53@gmail.com
 */
@Component
public class ContentComment extends AbstractComment<ContentEnhance> {

    @Resource
    private ContentService contentService;

    @Resource
    private MemberIntegralService memberIntegralService;

    public ContentComment(MemberService memberService) {
        super(memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId, Long floorNum) {
        String msgId = getMsgId();
        // 文章信息
        ContentEnhance contentEnhance = getTarget(targetId);
        Content target = contentEnhance.getContent();
        String contentId = target.getContentId();
        String contentTitle = target.getTitle();
        Member contentAuthor = getMemberInfo(target.getUserId()).getMember();

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId).getMember();
        String commenterUserName = commenter.getUserName();

        // 如果文章作者自己评论自己就不用发邮件了
        if (commenter.getUserId().equals(target.getUserId())) {
            return;
        }

        ContentLinkParamDto contentLinkParamDto = ContentLinkParamDto.builder()
                .contentId(contentId)
                .msgId(msgId)
                .floorNum(floorNum)
                .contentTitle(contentTitle)
                .build();
        String buildContentMsg = LinkBuilderInstance.buildLink(BusinessLinkType.CONTENT, contentLinkParamDto, MsgType.INNER_MSG);
        String buildContentMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.CONTENT, contentLinkParamDto, MsgType.EMAIL);


        MemberProfileLinkParamDto memberProfileLinkParamDto = MemberProfileLinkParamDto.builder()
                .memberUserId(commenterUserId)
                .msgId(msgId)
                .floorNum(floorNum)
                .memberUserName(commenterUserName)
                .build();
        String buildProfileMsg = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.EMAIL);

        // 站内信通知作者
        String innerMsgText = "您的文章《" + buildContentMsg + "》有新的评论,来自" + buildProfileMsg;
        // 邮件内容
        String emailContent = "点击《" + buildContentMsgEmail + "》查看评论,评论来自 " + buildProfileMsgEmail;

        MessageSend.send(MessageModel.builder()
                .toUserId(contentAuthor.getUserId())
                .emailModel(MessageModel.EmailModel.builder()
                        .title("您的文章有了新评论,快去看看吧")
                        .content(emailContent)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(innerMsgText).build())
                .messageType(MessageType.SYSTEM)
                .messageId(msgId)
                .build());

        // 送积分
        // 赠送积分描述
        String integralText = "您评论了 《" + buildContentMsg + "》文章,赠送 " +
                IntegralEnum.CREATE_COMMENT.getIntegral() + " 积分;快去写文章吧,您收到的评论越多,就会获得更多的积分~";
        memberIntegralService.addIntegral(IntegralEnum.CREATE_COMMENT, integralText, commenterUserId, targetId);
    }

    @Override
    protected ContentEnhance getTarget(String targetId) {
        return contentService.getNormalContent(targetId);
    }

    @Override
    public Boolean confirmSource(ContentType contentType, String targetId) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.contentSource().contains(contentType);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        ContentEnhance contentEnhance = getTarget(targetId);
        Content content = contentEnhance.getContent();
        content.setLatestCommentTime(CcDateUtil.getCurrentTime());
        content.setLatestCommentUserId(commenterUserId);
        contentService.updateContent(Converter.contentEnhance(content));
    }
}
