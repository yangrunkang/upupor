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

package com.upupor.service.business.message.channel;

import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.types.OpenEmail;
import com.upupor.service.base.MemberService;
import com.upupor.service.business.message.AbstractMessage;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.listener.event.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.upupor.framework.CcConstant.NOTIFY_ADMIN;
import static com.upupor.framework.CcConstant.UPUPOR_EMAIL;

/**
 * 邮件通知
 *
 * @author Yang Runkang (cruise)
 * @date 2022年11月10日
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Email extends AbstractMessage {

    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Boolean isSend(MessageModel messageModel) {
        return Objects.nonNull(messageModel.getEmailModel());
    }

    @Override
    public void send(MessageModel messageModel) {

        MessageModel.DirectEmailModel directEmailModel = messageModel.getDirectEmailModel();
        MessageModel.EmailModel emailModel = messageModel.getEmailModel();

        String email;
        if (Objects.nonNull(directEmailModel)) {
            email = directEmailModel.getEmail();
        } else {
            String toUserId = messageModel.getToUserId();
            if (NOTIFY_ADMIN.equals(toUserId)) {
                email = UPUPOR_EMAIL;
            } else {
                MemberEnhance memberEnhance = memberService.memberInfo(toUserId);
                boolean openEmail = OpenEmail.SUBSCRIBE_EMAIL.equals(memberEnhance.getMemberConfigEnhance().getMemberConfig().getOpenEmail());
                if (!openEmail) {
                    log.warn("用户:{}未开启邮件", toUserId);
                    return;
                }
                email = memberEnhance.getMember().getEmail();
            }
        }

        try {
            EmailEvent sendEmailEvent = new EmailEvent();
            sendEmailEvent.setToAddress(email);
            sendEmailEvent.setTitle(emailModel.getTitle());
            sendEmailEvent.setContent(emailModel.getContent());
            eventPublisher.publishEvent(sendEmailEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

