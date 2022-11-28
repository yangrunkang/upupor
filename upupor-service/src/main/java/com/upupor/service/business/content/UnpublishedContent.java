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

package com.upupor.service.business.content;

import com.upupor.data.dao.entity.ContentData;
import com.upupor.data.dao.entity.Draft;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.types.ContentStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.utils.ServletUtils;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.DraftService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.framework.ErrorCode.ARTICLE_NOT_BELONG_TO_YOU;
import static com.upupor.framework.ErrorCode.DRAFT_NOT_EXISTS;

/**
 * 未公开的内容
 *
 * @author cruise
 * @createTime 2021-12-31 18:03
 */
@Component
public class UnpublishedContent extends AbstractContent {
    @Resource
    private DraftService draftService;

    @Resource
    private ContentService contentService;

    @Override
    protected ContentEnhance queryContent() {
        ContentEnhance contentEnhance = null;
        Draft draft = draftService.getByDraftId(getContentId());
        if (Objects.isNull(draft)) { // 草稿不存在,则查询文章仅自己可见的内容
            contentEnhance = contentService.getManageContentDetail(getContentId());
            if (Objects.isNull(contentEnhance) || !ContentStatus.ONLY_SELF_CAN_SEE.equals(contentEnhance.getContent().getStatus())) {
                throw new BusinessException(DRAFT_NOT_EXISTS);
            }
        } else {
            contentEnhance = Draft.parseContent(draft);
            contentEnhance.setContentData(ContentData.empty(contentEnhance.getContent().getContentId()));
        }

        // 校验文章所属人
        String userId = ServletUtils.getUserId();
        if (!contentEnhance.getContent().getUserId().equals(userId)) {
            throw new BusinessException(ARTICLE_NOT_BELONG_TO_YOU);
        }
        return contentEnhance;
    }

    @Override
    protected void individuateBusiness() {

    }
}
