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

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author cruise
 * @createTime 2021-12-31 18:03
 */
@Getter
public enum ViewTargetType {

    /**
     * 0-内容(包含文章和电台)  1-个人主页  2-留言板
     */
    CONTENT(0, "内容"),
    PROFILE_CONTENT(1, "个人主页·文章"),
    PROFILE_MESSAGE(2, "个人主页·留言板"),
    PROFILE_RADIO(3, "个人主页·电台"),
    PROFILE_ATTENTION(4, "个人主页·关注"),
    PROFILE_FANS(5, "个人主页·粉丝"),
    RADIO(6, "电台"),
    ;
    @EnumValue
    private final Integer type;
    private final String name;

    ViewTargetType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}