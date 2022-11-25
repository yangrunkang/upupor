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

package com.upupor.service.listener;

import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.data.dto.email.EmailEvent;
import com.upupor.data.dto.email.EmailTemplateReplaceAndSendEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 消息事件总线
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:21
 */
@Slf4j
@Component
public class EmailListener {
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @EventListener
    @Async
    public void sendEmail(EmailEvent emailEvent) {
        if (StringUtils.isEmpty(emailEvent.getToAddress())) {
            log.error("发件地址为空");
            return;
        }
        if (StringUtils.isEmpty(emailEvent.getTitle())) {
            log.error("发件标题为空");
            return;
        }
        if (StringUtils.isEmpty(emailEvent.getContent())) {
            log.error("发件内容为空");
            return;
        }

        // 0-关闭 1-开启
        String property = SpringContextUtils.getBean(UpuporConfig.class).getEmail().getOnOff().toString();
        if (property.equals(CcConstant.CV_OFF)) {
            log.info("邮件开关已关闭");
            log.error("发送邮件日志[未真实发送邮件]: \n收件人:{},\n文章标题:{},\n邮件内容:{}", emailEvent.getToAddress(), emailEvent.getTitle(), emailEvent.getContent());
        } else if (property.equals(CcConstant.CV_ON)) {
            EmailTemplateReplaceAndSendEvent templateReplaceAndSendEvent = new EmailTemplateReplaceAndSendEvent();
            templateReplaceAndSendEvent.setTitle(emailEvent.getTitle());
            templateReplaceAndSendEvent.setContent(emailEvent.getContent());
            templateReplaceAndSendEvent.setToAddress(emailEvent.getToAddress());
            eventPublisher.publishEvent(templateReplaceAndSendEvent);
        }
    }


}
