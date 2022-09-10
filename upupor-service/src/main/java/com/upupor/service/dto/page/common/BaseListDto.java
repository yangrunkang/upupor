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

package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.utils.PageUtils;
import com.upupor.service.utils.page.PageDto;
import lombok.Data;

import java.util.List;

/**
 * List列表基类
 *
 * @author: Yang Runkang(cruise)
 * @email: yangrunkang53@gmail.com
 * @create: 2021-12-09 00:24
 */
@Data
public class BaseListDto {
    private Long total = 0L;
    private List<PageDto> pageDtoList;

    public BaseListDto() {
    }

    public BaseListDto(PageInfo pageInfo) {
        this.total = pageInfo.getTotal();
        this.pageDtoList = PageUtils.buildPageDtoList(pageInfo);
    }
}
