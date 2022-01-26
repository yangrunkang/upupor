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

package com.upupor.service.business.manage.business;

import com.upupor.framework.CcConstant;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dto.page.common.ListMessageDto;
import com.upupor.service.outer.req.ListMessageReq;
import com.upupor.service.types.MessageStatus;
import com.upupor.service.utils.ServletUtils;
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
            listMessageReq.setStatus(MessageStatus.UN_READ);
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

    @Override
    public String viewName() {
        return CcConstant.UserManageView.USER_MANAGE_MESSAGE;
    }

    @Override
    public String viewDesc() {
        return "个人消息";
    }
}