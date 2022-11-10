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

import com.upupor.service.business.message.AbstractMessage;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.dto.email.SendEmailEvent;
import com.upupor.service.types.OpenEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        return messageModel.getIsSendEmail();
    }

    @Override
    public void send(MessageModel messageModel) {
        String toUserId = messageModel.getToUserId();
        if (StringUtils.isEmpty(toUserId)) {
            return;
        }


        String email;
        if (StringUtils.isEmpty(messageModel.getToEmail())) {
            Member member = memberService.memberInfo(toUserId);
            email = member.getEmail();
            // 例如反馈邮件通知只发送给系统管理员
            if (NOTIFY_ADMIN.equals(toUserId)) {
                email = UPUPOR_EMAIL;
            } else {
                // 如果不允许直接跳过,则检查用户是否开始了邮件
                boolean openEmail = OpenEmail.SUBSCRIBE_EMAIL.equals(member.getMemberConfig().getOpenEmail());
                if (!openEmail) {
                    log.warn("用户:{}未开启邮件", toUserId);
                    return;
                }
            }
        } else {
            email = messageModel.getToEmail();
        }

        try {
            SendEmailEvent sendEmailEvent = new SendEmailEvent();
            sendEmailEvent.setToAddress(email);
            sendEmailEvent.setTitle(messageModel.getEmailTitle());
            sendEmailEvent.setContent(messageModel.getEmailContent());
            eventPublisher.publishEvent(sendEmailEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

