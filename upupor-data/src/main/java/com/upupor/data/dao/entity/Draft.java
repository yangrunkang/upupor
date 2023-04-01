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

package com.upupor.data.dao.entity;

import com.alibaba.fastjson2.JSONObject;
import com.upupor.data.dao.BaseEntity;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.DraftSource;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.DeflaterUtils;
import com.upupor.framework.utils.JsonUtils;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 草稿
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-07-20 00:14
 * @email: yangrunkang53@gmail.com
 */
@Data
public class Draft extends BaseEntity {
    private String userId;
    private String draftId;
    private String title;
    private String draftContent;
    private DraftSource draftSource;
    private Long createTime;


    /**
     * 草稿内容解析为内容
     *
     * @param draft
     * @return
     */
    public static ContentEnhance parseContent(Draft draft) {
        draft.unZip();
        String draftContent = draft.getDraftContent();
        String contentId = draft.getDraftId();
        String userId = draft.getUserId();

        if (!DraftSource.CONTENT.equals(draft.getDraftSource())) {
            throw new BusinessException(ErrorCode.DRAFT_SOURCE_NOT_EXISTS);
        }

        if (StringUtils.isEmpty(draftContent)) {
            return new ContentEnhance();
        }

        Content content = JsonUtils.parse2Clazz(draftContent, Content.class);
        content.setContentId(contentId);
        content.setUserId(userId);
        content.setStatus(ContentStatus.DRAFT); // 兼容
        content.setCreateTime(draft.getCreateTime());
        JSONObject jsonObject = JsonUtils.parse2JsonObject(draftContent);
        return ContentEnhance.builder()
                .content(content)
                .contentExtendEnhance(Converter.contentExtendEnhance(ContentExtend.create(contentId, jsonObject.getString("content"), jsonObject.getString("mdContent"))))
                .contentEditReasonEnhance(Converter.enhanceEditReason(ContentEditReason.create(contentId, jsonObject.getString("editReason"))))
                .build();
    }

    public void zip() {
        this.draftContent = DeflaterUtils.zipString(draftContent);
    }

    public void unZip() {
        this.draftContent = DeflaterUtils.unzipString(draftContent);
    }
}
