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
import com.upupor.data.dao.entity.Draft;
import com.upupor.service.base.DraftService;
import com.upupor.service.outer.req.content.AutoSaveContentReq;
import com.upupor.service.outer.req.content.CleanDraftReq;
import com.upupor.service.outer.req.content.ExistsDraftReq;
import com.upupor.framework.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 编辑器相关接口
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/12 05:22
 */
@Api(tags = "编辑器接口")
@RestController
@RequestMapping("/editor")
@RequiredArgsConstructor
public class EditorController {

    @Resource
    private DraftService draftService;

    @PostMapping("/auto-save")
    @ApiOperation("自动保存内容")
    @UpuporLimit(limitType = LimitType.CONTENT_AUTO_SAVE)
    public CcResponse autoSave(AutoSaveContentReq autoSaveContentReq) {
        CcResponse cc = new CcResponse();
        Boolean autoSave = draftService.autoSaveContent(autoSaveContentReq);
        cc.setData(autoSave);
        return cc;
    }


    /**
     * 清除草稿
     *
     * @return
     */
    @PostMapping("/clean-draft")
    public CcResponse cleanDraft(CleanDraftReq cleanDraftReq) {
        String contentId = cleanDraftReq.getContentId();
        String userId = ServletUtils.getUserId();

        Draft draft = draftService.getByDraftIdAndUserId(contentId, userId);
        if (Objects.nonNull(draft)) {
            draftService.delete(draft.getId());
        }

        return new CcResponse();
    }

    /**
     * 清除草稿
     *
     * @return
     */
    @PostMapping("/exists-draft")
    public CcResponse existsDraft(ExistsDraftReq existsDraftReq) {
        String contentId = existsDraftReq.getContentId();
        String userId = ServletUtils.getUserId();
        return new CcResponse(Objects.nonNull(draftService.getByDraftIdAndUserId(contentId, userId)));
    }

}
