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

package com.upupor.data.types;

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import lombok.Getter;

import static com.upupor.data.types.SearchContentType.SEARCH_CONTENT_URL.*;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年04月07日 01:20
 * @email: yangrunkang53@gmail.com
 */
@Getter
public enum SearchContentType {
    // 所有文章
    ALL(ALL_CONTENT_VIEW_URL),
    // 最近编辑过的
    RECENTLY_EDITED(RECENTLY_EDITED_CONTENT_VIEW_URL),
    // 新文章
    NEW(NEW_CONTENT_VIEW_URL),
    // 转载
    REPRINT(REPRINT_CONTENT_VIEW_URL);

    private final String url;

    SearchContentType(String url) {
        this.url = url;
    }

    public static SearchContentType getByUrl(String url) {
        for (SearchContentType value : values()) {
            if (value.getUrl().equals(url)) {
                return value;
            }
        }
        throw new BusinessException(ErrorCode.UNKNOWN_TYPE_CONTENT);
    }

    public static class SEARCH_CONTENT_URL {
        public static final String ALL_CONTENT_VIEW_URL = "/content/all";
        public static final String RECENTLY_EDITED_CONTENT_VIEW_URL = "/content/recently-edited";
        public static final String NEW_CONTENT_VIEW_URL = "/content/new";
        public static final String REPRINT_CONTENT_VIEW_URL = "/content/reproduce";
    }


}
