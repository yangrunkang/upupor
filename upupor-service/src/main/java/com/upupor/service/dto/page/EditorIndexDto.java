/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.service.dto.page;

import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.types.ContentType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑器页面Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/07 00:21
 */
@Data
public class EditorIndexDto {

    /**
     * 来源
     */
    private ContentType contentType;

    /**
     * 须知
     * 主要是写一些注意事项或者建议
     */
    private String instructions;

    /**
     * 特性 编辑器页面的特性
     */
    private String features;

    /**
     * 所有的标签
     */
    private List<Tag> tagList;

    /**
     * 创建文章-指定的Tag
     */
    private String createTag;

    /**
     * 文章
     */
    private Content content;

    /**
     * 根据当前用户点击的文章类型来创建内容
     */
    private CommonAggregateService.HrefDesc createContentDesc;

    public EditorIndexDto() {
        this.tagList = new ArrayList<>();
        this.content = new Content();
    }
}
