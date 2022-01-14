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

package com.upupor.service.business.aggregation.service;

import com.upupor.service.business.aggregation.dao.entity.Message;
import com.upupor.service.dto.page.common.ListMessageDto;
import com.upupor.service.outer.req.ListMessageReq;
import com.upupor.service.outer.req.UpdateMessageReq;
import com.upupor.service.types.MessageType;

/**
 * 消息服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 00:26
 */
public interface MessageService {

    /**
     * 添加消息
     *
     * @param toUserId   消息给到的目标用户
     * @param msgContent
     * @param messageType
     * @return
     */
    Integer addMessage(String toUserId, String msgContent, MessageType messageType, String msgId);

    /**
     * 发送邮件
     *
     * @param email
     * @param emailTitle
     * @param emailContent
     * @return
     */
    void sendEmail(String email, String emailTitle, String emailContent, String userId);

    /**
     * 更新消息
     *
     * @param updateMessageReq@return
     */
    Integer updateMessage(UpdateMessageReq updateMessageReq);

    /**
     * 获取消息列表
     *
     * @param listMessageReq
     * @return
     */
    ListMessageDto listMessage(ListMessageReq listMessageReq);

    /**
     * 获取消息
     *
     *
     * @param messageId@return
     */
    Message getMessage(String messageId);

    /**
     * 未读消息数
     *
     * @return
     */
    Integer unReadMessageTotal(ListMessageReq listMessageReq);

    /**
     * 标记消息为已读
     *
     * @param msgIdStr
     * @return
     */
    Integer tagMsgRead(String msgIdStr);

}