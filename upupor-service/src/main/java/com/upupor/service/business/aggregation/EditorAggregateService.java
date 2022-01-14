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

package com.upupor.service.business.aggregation;

import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.TagService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.dto.page.EditorIndexDto;
import com.upupor.service.outer.req.GetEditorReq;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.upupor.service.business.aggregation.CommonAggregateService.getCreateContentInfo;

/**
 * 编辑器页面
 *
 * @author runkangyang (cruise)
 * @date 2020.01.07 00:25
 */
@Service
@RequiredArgsConstructor
public class EditorAggregateService {

    private final TagService tagService;
    private final ContentService contentService;

    /**
     * 编辑器首页
     *
     * @param getEditorReq 从哪里进入编辑器
     * @return 返回EditorIndexDto
     */
    public EditorIndexDto index(GetEditorReq getEditorReq) {
        EditorIndexDto editorIndexDto = new EditorIndexDto();
        editorIndexDto.setTagList(tagService.getTagsByType(getEditorReq.getContentType()));
        editorIndexDto.setCreateTag(getEditorReq.getTag());

        if (Objects.nonNull(getEditorReq.getContentId())) {

            Content content = contentService.getManageContentDetail(getEditorReq.getContentId());

            if (Objects.isNull(content)) {
                throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
            }

            // 校验文章是否是作者本人的
            if (!content.getUserId().equals(ServletUtils.getUserId())) {
                throw new BusinessException(ErrorCode.BAN_EDIT_OTHERS_CONTENT);
            }
            editorIndexDto.setContent(content);
        }


        editorIndexDto.setCreateContentDesc(getCreateContentInfo(getEditorReq.getContentType(), getEditorReq.getTag()));

        return editorIndexDto;
    }
}
