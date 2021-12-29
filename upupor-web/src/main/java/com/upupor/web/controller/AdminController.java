package com.upupor.web.controller;

import com.upupor.service.common.CcResponse;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.service.aggregation.service.ContentService;
import com.upupor.spi.req.SetKeywordsReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


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

    @ApiOperation("设置关键字")
    @PostMapping(value = "/content/keywords")
    @ResponseBody
    public CcResponse add(SetKeywordsReq setKeywordsReq) {

        String contentId = setKeywordsReq.getContentId();
        String keywords = setKeywordsReq.getKeywords();
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        content.setKeywords(keywords);
        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(contentService.updateContent(content));
        return ccResponse;
    }

}
