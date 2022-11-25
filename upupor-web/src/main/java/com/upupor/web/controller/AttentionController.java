/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.web.controller;

import com.upupor.framework.CcResponse;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.base.AttentionService;
import com.upupor.service.outer.req.AddAttentionReq;
import com.upupor.service.outer.req.DelAttentionReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @UpuporLimit(limitType = LimitType.CLICK_ATTENTION, needSpendMoney = true)
    public CcResponse add(AddAttentionReq addAttentionReq) {
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(attentionService.attention(addAttentionReq));
        return ccResponse;
    }


    @ApiOperation("删除关注")
    @PostMapping(value = "/del")
    public CcResponse del(DelAttentionReq delAttentionReq) {
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(attentionService.delAttention(delAttentionReq));
        return ccResponse;
    }


}
