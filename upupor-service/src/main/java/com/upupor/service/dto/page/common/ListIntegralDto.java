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

package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.MemberIntegral;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户积分信息
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/08 14:22
 */
@Data
public class ListIntegralDto extends BaseListDto {
    /**
     * 总积分
     */
    private Integer totalIntegral = 0;
    private List<MemberIntegral> memberIntegralList;


    public ListIntegralDto(PageInfo pageInfo) {
        super(pageInfo);
        this.memberIntegralList = new ArrayList<>();
    }

    public ListIntegralDto() {
        this.memberIntegralList = new ArrayList<>();
    }
}
