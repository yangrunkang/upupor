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

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-07-10 17:17
 * @email: yangrunkang53@gmail.com
 */
@Data
public class AutoSaveContentReq extends ContentInterface {
    private Boolean isNewContent;
    /**
     * 预置文章Id
     * note: 父类也有该字段,这里依然写改字段,用于突出适用场景
     */
    private String preContentId;

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
     * 草稿内容
     */
    private String draftDetailContent;

    /**
     * 草稿内容markdown
     */
    private String draftMarkdownContent;

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
