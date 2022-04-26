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

import com.upupor.service.data.aggregation.service.ApplyService;
import com.upupor.service.business.apply.AdApply;
import com.upupor.service.business.apply.ConsultantApply;
import com.upupor.service.business.apply.TagApply;
import com.upupor.framework.CcResponse;
import com.upupor.service.outer.req.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 申请控制层
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 11:19
 */
@Api(tags = "申请服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/apply")
public class ApplyController {
    private final ApplyService applyService;
    private final AdApply adApply;
    private final ConsultantApply consultantApply;
    private final TagApply tagApply;


    @ApiOperation("添加广告申请")
    @PostMapping(value = "/addAd")
    @ResponseBody
    public CcResponse addAd(AddApplyReq adApplyReq) {
        return new CcResponse(adApply.doBusiness(adApplyReq));
    }

    @ApiOperation("添加咨询服务申请")
    @PostMapping(value = "/addConsultant")
    @ResponseBody
    public CcResponse addConsultant(AddConsultantReq addConsultantReq) {
        return new CcResponse(consultantApply.doBusiness(addConsultantReq));
    }

    @ApiOperation("添加标签申请")
    @PostMapping(value = "/addTag")
    @ResponseBody
    public CcResponse addTag(AddTagReq addTagReq) {
        return new CcResponse(tagApply.doBusiness(addTagReq));
    }

    @ApiOperation("删除申请")
    @PostMapping(value = "/del")
    @ResponseBody
    public CcResponse del(DelApplyReq delApplyReq) {
        return new CcResponse(applyService.delApply(delApplyReq));
    }

    @ApiOperation("编辑申请")
    @PostMapping(value = "/edit")
    @ResponseBody
    public CcResponse edit(UpdateApplyReq updateApplyReq) {
        return new CcResponse(applyService.editApply(updateApplyReq));
    }

    @ApiOperation("提交申请材料")
    @PostMapping(value = "/commit")
    @ResponseBody
    public CcResponse edit(AddApplyDocumentReq addApplyDocumentReq) throws Exception {
        return new CcResponse(applyService.commitDocument(addApplyDocumentReq) > 0);
    }

}
