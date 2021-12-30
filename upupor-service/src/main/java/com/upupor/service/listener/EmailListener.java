/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

import com.upupor.service.dto.email.SendEmailEvent;
import com.upupor.service.utils.CcEmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 消息事件总线
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:21
 */
@Slf4j
@Component
public class EmailListener {

    @EventListener
    @Async
    public void sendEmail(SendEmailEvent sendEmailEvent) {
        if (StringUtils.isEmpty(sendEmailEvent.getToAddress())) {
            log.error("发件地址为空");
            return;
        }
        if (StringUtils.isEmpty(sendEmailEvent.getTitle())) {
            log.error("发件标题为空");
            return;
        }
        if (StringUtils.isEmpty(sendEmailEvent.getContent())) {
            log.error("发件内容为空");
            return;
        }

        // 内容重新使用模板内容
        CcEmailUtils.sendEmail(sendEmailEvent);
    }


}
