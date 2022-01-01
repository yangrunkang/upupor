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

package com.upupor.service.dto.page.sitedata;

import lombok.Data;

/**
 * 网站业务数据
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 03:46
 */
@Data
public class BusinessData {

    private Integer contentTotal;

    private Integer commentTotal;

    private Integer onlineUserTotal;

    private Integer advertisingTotal;

    private Integer loginTimes;

    private Integer registerNum;

    private Integer viewContentTotal;

    public BusinessData() {
        this.contentTotal = 0;
        this.commentTotal = 0;
        this.onlineUserTotal = 0;
        this.advertisingTotal = 0;
        this.loginTimes = 0;
        this.registerNum = 0;
        this.viewContentTotal = 0;
    }
}
