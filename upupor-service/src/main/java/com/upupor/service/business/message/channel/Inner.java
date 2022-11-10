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

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.message.AbstractMessage;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.data.dao.entity.Message;
import com.upupor.service.data.dao.mapper.MessageMapper;
import com.upupor.service.types.MessageStatus;
import com.upupor.service.types.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 站内信通知
 *
 * @author Yang Runkang (cruise)
 * @date 2022年11月10日
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Inner extends AbstractMessage {

    private final MessageMapper messageMapper;

    @Override
    public void send(MessageModel messageModel) {
        String msgId = messageModel.getMessageId();
        MessageType messageType = messageModel.getMessageType();
        try {
            Message message = new Message();
            message.setMessageId(msgId);
            message.setMessage(messageModel.getInnerMsgText());
            message.setUserId(messageModel.getToUserId());
            if (Objects.isNull(messageType)) {
                messageType = MessageType.SYSTEM;
            }
            message.setMessageType(messageType);
            message.setStatus(MessageStatus.UN_READ);
            message.setCreateTime(CcDateUtil.getCurrentTime());
            message.setSysUpdateTime(new Date());
            messageMapper.insert(message);
        } catch (Exception ex) {
            log.error("站内信发送异常", ex);
        }
    }
}
