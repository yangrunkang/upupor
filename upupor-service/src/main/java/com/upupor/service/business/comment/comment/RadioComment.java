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

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.comment.AbstractComment;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Radio;
import com.upupor.service.data.service.CommentService;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.data.service.MessageService;
import com.upupor.service.data.service.RadioService;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.framework.CcConstant.MsgTemplate.*;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:21
 * @email: yangrunkang53@gmail.com
 */
@Component
public class RadioComment extends AbstractComment<Radio> {
    @Resource
    private RadioService radioService;

    @Resource
    private MessageService messageService;

    public RadioComment(CommentService commentService, MemberService memberService) {
        super(memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId, String commentId) {
        String msgId = getMsgId();

        Radio radio = radioService.getByRadioId(targetId);
        String radioName = radio.getRadioIntro();

        Member radioAuthor = getMemberInfo(radio.getUserId());
        String radioAuthorUserId = radioAuthor.getUserId();

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId);
        String commenterUserName = commenter.getUserName();

        // 如果作者自己评论自己就不用发邮件了
        if (commenter.getUserId().equals(radio.getUserId())) {
            return;
        }

        // 站内信通知对方收到新的留言
        String msg = "您收到了新的电台评论,点击<strong>《" + String.format(RADIO_INTEGRAL, radio.getRadioId(), msgId, radioName) + "》</strong>查看,评论来自"
                + String.format(PROFILE_INNER_MSG, commenterUserId, msgId, commenterUserName);


        // 发送邮件通知对方收到新的留言
        String emailTitle = "您有新的电台评论,快去看看吧";
        String emailContent = "点击" + String.format(RADIO_EMAIL, radio.getRadioId(), msgId, radioName) + ",评论来自 "
                + String.format(PROFILE_EMAIL, commenterUserId, msgId, commenterUserName);

        MessageSend.send(MessageModel.builder()
                .toUserId(radioAuthorUserId)
                .messageType(MessageType.USER_REPLAY)
                .emailModel(MessageModel.EmailModel.builder()
                        .title(emailTitle)
                        .content(emailContent)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(msg).build())
                .messageId(msgId)
                .build());
    }

    @Override
    protected Radio getTarget(String targetId) {
        return radioService.getByRadioId(targetId);
    }

    @Override
    public Boolean confirmSource(ContentType contentType, String targetId) {
        return Objects.nonNull(getTarget(targetId)) && ContentType.RADIO.equals(contentType);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        Radio radio = getTarget(targetId);
        radio.setLatestCommentTime(CcDateUtil.getCurrentTime());
        radio.setLatestCommentUserId(commenterUserId);
        radioService.updateRadio(radio);
    }
}
