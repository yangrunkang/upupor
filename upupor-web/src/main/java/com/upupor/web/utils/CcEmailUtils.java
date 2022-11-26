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

package com.upupor.web.utils;

import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.business.email.TrueSend;
import com.upupor.framework.common.CcTemplateConstant;
import com.upupor.service.listener.event.EmailTemplateReplaceAndSendEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 23:48
 */
@Slf4j
public class CcEmailUtils {


    /**
     * 发送邮件
     *
     * @param trueSendEvent
     */
    public static void sendEmail(EmailTemplateReplaceAndSendEvent trueSendEvent) {
        // 接入模板
        Map<String, Object> maps = new HashMap<>(2);
        maps.put(CcTemplateConstant.TITLE, trueSendEvent.getTitle());
        maps.put(CcTemplateConstant.CONTENT, trueSendEvent.getContent());
        String content = HtmlTemplateUtils.renderEmail(CcTemplateConstant.TEMPLATE_EMAIL, maps);
        // 内容重新使用模板的内容
        trueSendEvent.setContent(content);

        TrueSend trueSend = SpringContextUtils.getBean(TrueSend.class);
        trueSend.trueSend(trueSendEvent.getToAddress(), trueSendEvent.getTitle(), null, trueSendEvent.getContent());
    }


}
