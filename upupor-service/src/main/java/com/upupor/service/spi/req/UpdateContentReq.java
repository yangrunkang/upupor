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

import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.OriginType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:44
 */
@Data
public class UpdateContentReq {

    @NotEmpty(message = "文章Id为空")
    private String contentId;

    private String title;

    private String shortContent;

    private ContentType contentType;

    private ContentType tagType;

    private String detailContent;

    private String mdContent;

    private ContentStatus status;

    private String userId;

    /**
     * 标签
     */
    private String tagIds;

    /**
     * 编辑原因
     */
    private String editReason;

    private OriginType originType;
    private String noneOriginLink;

    /**
     * 是否将草稿公开 true-公开
     */
    private Boolean isDraftPublic;

}
