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

package com.upupor.service.types;

import com.upupor.service.business.pages.content.AllContentView;
import com.upupor.service.business.pages.content.NewContentView;
import com.upupor.service.business.pages.content.RecentlyEditedContentView;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import lombok.Getter;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年04月07日 01:20
 * @email: yangrunkang53@gmail.com
 */
@Getter
public enum SearchContentType {
    // 所有文章
    ALL(AllContentView.URL),
    // 最近编辑过的
    RECENTLY_EDITED(RecentlyEditedContentView.URL),
    // 所有新文章
    NEW(NewContentView.URL)
    ;

    private final String url;

    SearchContentType(String url) {
        this.url = url;
    }


    public static SearchContentType getByUrl(String url){
        for (SearchContentType value : values()) {
            if(value.getUrl().equals(url)){
                return value;
            }
        }
        throw new BusinessException(ErrorCode.UNKNOWN_TYPE_CONTENT);
    }


}
