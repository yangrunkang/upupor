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

package com.upupor.service.data.dao.entity;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.outer.req.content.AutoSaveContentReq;
import com.upupor.service.types.DraftSource;
import lombok.Data;

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
    private String draftContent;
    private DraftSource draftSource;
    private Long createTime;


    public static Draft create(AutoSaveContentReq autoSaveContentReq, String userId) {
        Draft draft = new Draft();
        draft.setUserId(userId);
        draft.setDraftId(autoSaveContentReq.getDraftId());
        draft.setDraftContent(autoSaveContentReq.getDraftContent());
        draft.setDraftSource(autoSaveContentReq.getDraftSource());
        draft.setCreateTime(CcDateUtil.getCurrentTime());
        return draft;
    }

}
