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
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.listener.event.GenerateGoogleSiteMapEvent;
import com.upupor.service.outer.req.SetContentStatusReq;
import com.upupor.service.outer.req.SetKeywordsReq;
import com.upupor.service.types.ContentStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.upupor.framework.thread.completable.CompletableFuturePool.ASYNC;


/**
 * 活动控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 00:13
 */
@Api(tags = "活动服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ContentService contentService;
    private final ApplicationEventPublisher eventPublisher;

    @ApiOperation("设置关键字")
    @PostMapping(value = "/content/keywords")
    @ResponseBody
    public CcResponse setKeywords(SetKeywordsReq setKeywordsReq) {

        String contentId = setKeywordsReq.getContentId();
        String keywords = setKeywordsReq.getKeywords();
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        content.setKeywords(keywords);
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(contentService.updateContent(content));
        return ccResponse;
    }

    @ApiOperation("设置状态")
    @PostMapping(value = "/content/status")
    @ResponseBody
    public CcResponse setContentStatus(SetContentStatusReq setContentStatusReq) {

        String contentId = setContentStatusReq.getContentId();
        ContentStatus status = setContentStatusReq.getStatus();

        Content content = contentService.getContentByContentIdNoStatus(contentId);
        content.setStatus(status);

        Boolean result = contentService.updateContent(content);
        if (result) {
            // 重新生成站点地图
            eventPublisher.publishEvent(new GenerateGoogleSiteMapEvent());
        }

        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(result);
        return ccResponse;
    }

    @ApiOperation("cpu")
    @PostMapping(value = "/cpu")
    @ResponseBody
    public CcResponse cpu() {
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(ASYNC);
        return ccResponse;
    }

}
