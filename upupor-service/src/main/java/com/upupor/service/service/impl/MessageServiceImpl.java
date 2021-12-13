package com.upupor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Message;
import com.upupor.service.dao.mapper.MessageMapper;
import com.upupor.service.dto.email.SendEmailEvent;
import com.upupor.service.dto.page.common.ListMessageDto;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import com.upupor.spi.req.ListMessageReq;
import com.upupor.spi.req.UpdateMessageReq;
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

import static com.upupor.service.common.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;

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
    public Integer addMessage(String toUserId, String msgContent, Integer messageType, String msgId) {
        Assert.notNull(msgId, "msgId不能为空");

        try {
            Message message = new Message();
            message.setMessageId(msgId);
            message.setMessage(msgContent);
            message.setUserId(toUserId);
            if (Objects.isNull(messageType)) {
                messageType = CcEnum.MessageType.SYSTEM.getType();
            }
            message.setMessageType(messageType);
            message.setStatus(CcEnum.MessageStatus.UN_READ.getStatus());
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

        // SKIP_SUBSCRIBE_EMAIL_CHECK 是跳过是否订阅邮件配置校验
        if (!SKIP_SUBSCRIBE_EMAIL_CHECK.equals(userId)) {
            // 如果没有开启邮件订阅,则不发送邮件
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
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, updateMessageReq.getUserId());

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
        PageHelper.startPage(listMessageReq.getPageNum(), listMessageReq.getPageSize());
        List<Message> messages = messageMapper.listMessage(listMessageReq);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);

        ListMessageDto listMessageDto = new ListMessageDto(pageInfo);
        listMessageDto.setMessageList(pageInfo.getList());
        return listMessageDto;
    }

    @Override
    public Message getMessage(String messageId) {
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>()
                .eq(Message::getMessageId, messageId);
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

        if (CcEnum.MessageStatus.READ.getStatus().equals(message.getStatus())
                || CcEnum.MessageStatus.DELETED.getStatus().equals(message.getStatus())) {
            return 0;
        }
        message.setStatus(CcEnum.MessageStatus.READ.getStatus());
        return messageMapper.updateById(message);
    }
}
