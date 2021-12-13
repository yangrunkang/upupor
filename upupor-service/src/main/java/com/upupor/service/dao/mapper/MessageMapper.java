package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Message;
import com.upupor.spi.req.ListMessageReq;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    List<Message> listMessage(ListMessageReq listMessageReq);

    Integer listMessageCount(ListMessageReq listMessageReq);

}
