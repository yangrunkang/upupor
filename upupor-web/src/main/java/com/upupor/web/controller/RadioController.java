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

package com.upupor.web.controller;

import com.upupor.framework.CcResponse;
import com.upupor.lucene.UpuporLucene;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.base.RadioService;
import com.upupor.data.dto.OperateRadioDto;
import com.upupor.service.outer.req.AddRadioReq;
import com.upupor.service.outer.req.DelRadioReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author YangRunkang(cruise)
 * @date 2020/11/15 19:08
 */
@Api(tags = "音频相关服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/radio")
public class RadioController {
    private final RadioService radioService;

    @ApiOperation("删除音频")
    @PostMapping(value = "/delete")
    @UpuporLucene(dataType = LuceneDataType.RADIO, operationType = LuceneOperationType.DELETE)
    public CcResponse deleteRadio(DelRadioReq delRadioReq) {
        OperateRadioDto operateRadioDto = radioService.deleteRadio(delRadioReq);

        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(operateRadioDto);
        return ccResponse;
    }


    @ApiOperation("添加音频记录")
    @PostMapping(value = "/add")
    @UpuporLucene(dataType = LuceneDataType.RADIO, operationType = LuceneOperationType.ADD)
    @UpuporLimit(limitType = LimitType.CREATE_RADIO)
    public CcResponse addRadio(AddRadioReq addRadioReq) {
        OperateRadioDto operateRadioDto = radioService.createNewRadio(addRadioReq);

        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(operateRadioDto);
        return ccResponse;
    }

}
