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

package com.upupor.data.utils.page;

import lombok.Data;

/**
 * 页码Dto
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-10 17:26
 * @email: yangrunkang53@gmail.com
 * @note 这里只返回页码元数据, 具体样式放在前端渲染, 解决网页SEO问题及IE页面渲染问题
 */
@Data
public class PageDto {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 页码名字
     */
    private String pageNumName;

    /**
     * 是否激活
     */
    private Boolean active = false;
    private Boolean last = false;

    public PageDto(Integer pageNum, String pageNumName, Boolean active) {
        this.pageNum = pageNum;
        this.pageNumName = pageNumName;
        this.active = active;
    }

    public PageDto(Integer pageNum, String pageNumName) {
        this.pageNum = pageNum;
        this.pageNumName = pageNumName;
    }

    public PageDto(Boolean last, Integer pageNum, String pageNumName) {
        this.last = last;
        this.pageNum = pageNum;
        this.pageNumName = pageNumName;
    }

    public PageDto() {
    }
}
