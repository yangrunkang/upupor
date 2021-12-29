package com.upupor.service.service.aggregation.service;

import com.upupor.service.dao.entity.Message;
import com.upupor.service.dto.page.common.ListMessageDto;
import com.upupor.spi.req.ListMessageReq;
import com.upupor.spi.req.UpdateMessageReq;

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
     * @return
     */
    Integer addMessage(String toUserId, String msgContent, Integer messageType, String msgId);

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