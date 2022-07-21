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

package com.upupor.service.outer.req.content;

import com.upupor.service.types.ContentType;
import com.upupor.service.types.OriginType;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月07日 22:10
 * @email: yangrunkang53@gmail.com
 */
@Data
public class BaseContentReq {
    /**
     * 预置文章Id
     */
    private String preContentId;

    @Length(max = 256, message = "文章标题过长,最多可输入256个字")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Length(max = 1024, message = "简介内容过长,最多可输入1024个字")
    private String shortContent;

    /**
     * 文章类型
     */
    private ContentType contentType;

    @Length(max = 256, message = "标签过多,请减少标签数目")
    private String tagIds;

    private String content;
    private String mdContent;

    /**
     * 原创
     */
    private OriginType originType;

    @Length(max = 1024, message = "转载链接过长,最多可输入1024个字")
    private String noneOriginLink;

    public String getMdContent() {
        if (StringUtils.isEmpty(mdContent)) {
            return Strings.EMPTY;
        }
        return mdContent;
    }
}
