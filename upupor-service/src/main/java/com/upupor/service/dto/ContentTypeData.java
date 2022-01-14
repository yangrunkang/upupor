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

package com.upupor.service.dto;

import com.upupor.service.types.ContentType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cruise
 */
@Data
public class ContentTypeData {
    private String typeName;
    private String name;
    private String url;
    private String webText;
    private String icon;
    private String tips;
    private String cssClass;

    public ContentTypeData() {
    }

    public ContentTypeData(String typeName, String name, String url, String webText, String icon, String tips,String cssClass) {
        this.typeName = typeName;
        this.name = name;
        this.url = url;
        this.webText = webText;
        this.icon = icon;
        this.tips = tips;
        this.cssClass = cssClass;
    }


    public List<ContentTypeData> contentTypeDataList(){
        List<ContentTypeData> list = new ArrayList<>();
        for (ContentType value : ContentType.contentSource()) {
            list.add(new ContentTypeData(
                    value.name(),
                    value.getName(),
                    value.getUrl(),
                    value.getWebText(),
                    value.getIcon(),
                    value.getTips(),
                    value.name().toLowerCase()));
        }
        return list;
    }
}
