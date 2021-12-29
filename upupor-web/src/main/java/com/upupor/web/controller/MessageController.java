package com.upupor.web.controller;

import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.common.CcResponse;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.UpdateMessageReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 消息
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:52
 */
@Api(tags = "消息服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;


    @ApiOperation("编辑消息")
    @ResponseBody
    @PostMapping("/edit")
    private CcResponse edit(UpdateMessageReq updateMessageReq) {
        CcResponse cc = new CcResponse();
        ServletUtils.checkOperatePermission(updateMessageReq.getUserId());

        Integer update = messageService.updateMessage(updateMessageReq);
        cc.setData(update > 0);
        return cc;
    }

}
