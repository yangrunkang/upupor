package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 16:54
 */
@Data
public class ListMessageDto extends BaseListDto {

    private List<Message> messageList;

    public ListMessageDto(PageInfo pageInfo) {
        super(pageInfo);
        this.messageList = new ArrayList<>();
    }

    public ListMessageDto() {
        this.messageList = new ArrayList<>();
    }
}
