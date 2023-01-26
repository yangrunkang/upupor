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
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.MessageType;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.service.base.MemberIntegralService;
import com.upupor.service.base.MemberService;
import com.upupor.service.business.comment.comment.abstracts.AbstractComment;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.framework.CcConstant.MsgTemplate.*;

/**
 * 留言板评论
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:22
 * @email: yangrunkang53@gmail.com
 */
@Component
public class MessageBoardComment extends AbstractComment<MemberEnhance> {

    @Resource
    private MemberIntegralService memberIntegralService;

    public MessageBoardComment(MemberService memberService) {
        super(memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId) {
        String msgId = getMsgId();

        // 获取被评论的用户
        Member targetMember = getTarget(targetId).getMember();
        String targetUserId = targetMember.getUserId();

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId).getMember();
        String commenterUserName = commenter.getUserName();

        // 如果作者自己评论自己就不用发邮件了
        if (commenter.getUserId().equals(targetMember.getUserId())) {
            return;
        }

        // 站内信通知对方收到新的留言
        String msg = "您收到了新的留言信息,点击" + buildMessageBoardMsg(targetUserId, msgId, "<strong>留言板</strong>") + "查看. 留言来自"
                + buildProfileMsg(commenterUserId, msgId, commenterUserName);

        // 发送邮件通知对方收到新的留言
        String emailTitle = "您有新的留言,快去看看吧";
        String emailContent = "点击" + buildMessageBoardMsgEmail(targetUserId, msgId, "<strong>留言板</strong>") + ",留言来自 "
                + buildProfileMsgEmail(commenterUserId, msgId, commenterUserName);

        MessageSend.send(MessageModel.builder()
                .toUserId(targetUserId)
                .emailModel(MessageModel.EmailModel.builder()
                        .title(emailTitle)
                        .content(emailContent)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(msg).build())
                .messageType(MessageType.USER_REPLAY)
                .messageId(msgId)
                .build());


        // 留言赠送积分
        IntegralEnum integralEnum = IntegralEnum.MESSAGE;
        String text = "您给 " + buildMessageBoardMsg(targetUserId, msgId, targetMember.getUserName()) + " 留言了,赠送 " +
                integralEnum.getIntegral() + " 积分;";
        memberIntegralService.addIntegral(integralEnum, text, commenterUserId, targetId);
    }

    @Override
    protected MemberEnhance getTarget(String targetId) {
        return getMemberInfo(targetId);
    }

    @Override
    public Boolean confirmSource(ContentType contentType, String targetId) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.MESSAGE.equals(contentType);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {

    }
}
