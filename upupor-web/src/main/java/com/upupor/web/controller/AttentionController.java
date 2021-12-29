package com.upupor.web.controller;

import com.upupor.service.business.aggregation.service.AttentionService;
import com.upupor.service.common.CcResponse;
import com.upupor.spi.req.AddAttentionReq;
import com.upupor.spi.req.DelAttentionReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 关注控制层
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 21:15
 */
@Slf4j
@Api(tags = "关注服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/attention")
public class AttentionController {
    private final AttentionService attentionService;

    @ApiOperation("添加关注")
    @PostMapping(value = "/add")
    @ResponseBody
    public CcResponse add(AddAttentionReq addAttentionReq) {
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(attentionService.attention(addAttentionReq));
        return ccResponse;
    }


    @ApiOperation("删除关注")
    @PostMapping(value = "/del")
    @ResponseBody
    public CcResponse del(DelAttentionReq delAttentionReq) {
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(attentionService.delAttention(delAttentionReq));
        return ccResponse;
    }


}
