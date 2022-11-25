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

package com.upupor.data.dto.page;

import com.upupor.data.types.ContentType;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.SpringContextUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年11月25日
 * @email: yangrunkang53@gmail.com
 */
@AllArgsConstructor
@Data
public class HrefDesc {
    private String desc;
    private String href;
    private String icon;
    private String tips;

    public HrefDesc(ContentType contentType, String tag, String tagName) {
        this.desc = contentType.getTips();
        this.href = "/editor?type=" + contentType.name();
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
        this.icon = upuporConfig.getOssStaticPrefix() + contentType.getIcon();
        this.tips = contentType.getTips();
        this.href = "/editor?type=" + contentType.name();
        if (!StringUtils.isEmpty(tag)) {
            this.href = this.getHref() + "&tag=" + tag;
        }
        if (!StringUtils.isEmpty(tagName)) {
            this.desc = contentType.getTips() + " >> " + tagName;
        }
    }

}
