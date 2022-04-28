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

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.data.dao.entity.Message;
import com.upupor.service.data.dao.mapper.MessageMapper;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.data.service.MessageService;
import com.upupor.service.dto.email.SendEmailEvent;
import com.upupor.service.dto.page.common.ListMessageDto;
import com.upupor.service.outer.req.ListMessageReq;
import com.upupor.service.outer.req.UpdateMessageReq;
import com.upupor.service.types.MessageStatus;
import com.upupor.service.types.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.upupor.framework.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;

/**
 * 消息服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 12:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Integer addMessage(String toUserId, String msgContent, MessageType messageType, String msgId) {
        Assert.notNull(msgId, "msgId不能为空");

        try {
            Message message = new Message();
            message.setMessageId(msgId);
            message.setMessage(msgContent);
            message.setUserId(toUserId);
            if (Objects.isNull(messageType)) {
                messageType = MessageType.SYSTEM;
            }
            message.setMessageType(messageType);
            message.setStatus(MessageStatus.UN_READ);
            message.setCreateTime(CcDateUtil.getCurrentTime());
            message.setSysUpdateTime(new Date());
            return messageMapper.insert(message);
        } catch (Exception e) {

        }
        // 返回默认值 0
        return BigDecimal.ZERO.intValue();
    }

    @Override
    public void sendEmail(String email, String emailTitle, String emailContent, String userId) {
        if (StringUtils.isEmpty(userId)) {
            return;
        }

        // 跳过配置检查,主要用于一些无用户邮件,例如反馈邮件通知
        if (!SKIP_SUBSCRIBE_EMAIL_CHECK.equals(userId)) {
            // 如果不允许直接跳过,则检查用户是否开始了邮件
            Boolean openEmail = memberService.isOpenEmail(userId);
            if (!openEmail) {
                log.warn("用户:{}未开启邮件", userId);
                return;
            }
        }

        try {
            SendEmailEvent sendEmailEvent = new SendEmailEvent();
            sendEmailEvent.setToAddress(email);
            sendEmailEvent.setTitle(emailTitle);
            sendEmailEvent.setContent(emailContent);
            eventPublisher.publishEvent(sendEmailEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer updateMessage(UpdateMessageReq updateMessageReq) {

        // 单个消息已读
        if (Objects.nonNull(updateMessageReq.getMessageId())) {
            Message message = getMessage(updateMessageReq.getMessageId());
            message.setStatus(updateMessageReq.getStatus());
            return messageMapper.updateById(message);
        }

        // 所有消息已读
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>().eq(Message::getUserId, updateMessageReq.getUserId());
        List<Message> messages = messageMapper.selectList(query);
        AtomicInteger updateCount = new AtomicInteger();
        messages.forEach(message -> {
            message.setStatus(updateMessageReq.getStatus());
            updateCount.addAndGet(messageMapper.updateById(message));
        });
        return updateCount.get();
    }

    @Override
    public ListMessageDto listMessage(ListMessageReq listMessageReq) {
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>().eq(Message::getUserId, listMessageReq.getUserId()).in(Message::getStatus, MessageStatus.all()).orderByDesc(Message::getCreateTime);
        if (Objects.nonNull(listMessageReq.getStatus())) {
            query.eq(Message::getStatus, listMessageReq.getStatus());
        }

        PageHelper.startPage(listMessageReq.getPageNum(), listMessageReq.getPageSize());
        List<Message> messages = messageMapper.selectList(query);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);

        ListMessageDto listMessageDto = new ListMessageDto(pageInfo);
        listMessageDto.setMessageList(pageInfo.getList());
        return listMessageDto;
    }

    @Override
    public Message getMessage(String messageId) {
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>().eq(Message::getMessageId, messageId);
        return messageMapper.selectOne(query);
    }

    @Override
    public Integer unReadMessageTotal(ListMessageReq listMessageReq) {
        return messageMapper.listMessageCount(listMessageReq);
    }

    @Override
    public Integer tagMsgRead(String msgId) {
        if (StringUtils.isEmpty(msgId)) {
            return 0;
        }

        Message message = getMessage(msgId);
        if (Objects.isNull(message)) {
            return 0;
        }

        if (MessageStatus.READ.equals(message.getStatus()) || MessageStatus.DELETED.equals(message.getStatus())) {
            return 0;
        }
        message.setStatus(MessageStatus.READ);
        return messageMapper.updateById(message);
    }
}
