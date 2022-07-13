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

package com.upupor.service.business.editor;

import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.ContentExtend;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.outer.req.content.AutoSaveContentReq;
import com.upupor.service.types.DraftStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-07-10 17:17
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
public class AutoSave extends AbstractEditor<AutoSaveContentReq> {

    @Override
    protected EditorType editorType() {
        return EditorType.AUTO_SAVE;
    }

    @Override
    protected void check() {

    }

    @Override
    protected OperateContentDto doBusiness() {
        AutoSaveContentReq autoSaveContentReq = getReq();

        Boolean isNewContent = autoSaveContentReq.getIsNewContent();
        String preContentId = autoSaveContentReq.getPreContentId();
        Content content = Content.createAutoSave(preContentId, autoSaveContentReq);
        content.setDraftStatus(DraftStatus.YES);
        draftContet(autoSaveContentReq, content);
        if (isNewContent) {
            // 先检查内容Id是否存在,如果存在则更新,否则则新增
            Content preContent = contentService.getContentByContentIdNoStatus(preContentId);
            if (Objects.isNull(preContent)) {
                contentService.insertContent(content);
            } else {
                contentService.updateContent(content); // 第一次保存后,第二次进入会到这里
            }
        } else {
            // 更新操作
            contentService.updateContent(content);
        }

        return OperateContentDto.builder()
                .contentId(content.getContentId())
                .success(Boolean.TRUE)
                .build();
    }

    private void draftContet(AutoSaveContentReq autoSaveContentReq, Content content) {
        ContentExtend contentExtend = content.getContentExtend();
        contentExtend.setDraftDetailContent(autoSaveContentReq.getDraftDetailContent());
        contentExtend.setDraftMarkdownContent(autoSaveContentReq.getDraftMarkdownContent());
    }

}
