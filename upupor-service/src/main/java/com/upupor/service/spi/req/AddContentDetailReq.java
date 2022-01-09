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

package com.upupor.service.spi.req;

import com.upupor.service.spi.req.content.BaseContentReq;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.OriginType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:44
 */
@Data
public class AddContentDetailReq  extends BaseContentReq {

    @Length(max = 256, message = "文章标题过长,最多可输入256个字")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Length(max = 1024, message = "简介内容过长,最多可输入1024个字")
    private String shortContent;

    private ContentType contentType;

    private String content;
    private String mdContent;

    @Length(max = 256, message = "标签过多,请减少标签数目")
    private String tagIds;

    /**
     * 操作 temp: 代表草稿
     */
    private String operation;

    private OriginType originType;

    @Length(max = 1024, message = "转载链接过长,最多可输入1024个字")
    private String noneOriginLink;

    private String preContentId;


}
