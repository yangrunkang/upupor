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

package com.upupor.service.data.aggregation;

import com.upupor.service.data.service.TagService;
import com.upupor.service.dto.page.EditorIndexDto;
import com.upupor.service.outer.req.GetEditorReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.upupor.service.data.aggregation.CommonAggregateService.getCreateContentInfo;

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
        editorIndexDto.setCreateContentDesc(getCreateContentInfo(getEditorReq.getContentType(), getEditorReq.getTag()));
        return editorIndexDto;
    }
}
