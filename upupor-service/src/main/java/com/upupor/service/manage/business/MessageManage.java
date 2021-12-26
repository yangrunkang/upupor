package com.upupor.service.manage.business;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dto.page.common.ListMessageDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.MessageService;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.ListMessageReq;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class MessageManage extends AbstractManageInfoGet {

    @Resource
    private MessageService messageService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();

        String userId = manageDto.getUserId();
        String status = manageDto.getMessageStatus();

        if (StringUtils.isEmpty(status)) {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }

        String unread = "un-read";
        String all = "all";

        ListMessageReq listMessageReq = new ListMessageReq();
        if (status.equals(unread)) {
            listMessageReq.setStatus(CcEnum.MessageStatus.UN_READ.getStatus());
        } else if (status.equals(all)) {
            // 所有消息
            listMessageReq.setStatus(null);
        } else {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }
        listMessageReq.setUserId(ServletUtils.getUserId());
        listMessageReq.setPageNum(pageNum);
        listMessageReq.setPageSize(pageSize);

        ListMessageDto listMessageDto = messageService.listMessage(listMessageReq);
        getMemberIndexDto().setListMessageDto(listMessageDto);

    }
}