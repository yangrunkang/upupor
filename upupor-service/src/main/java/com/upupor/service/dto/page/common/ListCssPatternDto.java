/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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
import com.upupor.service.dao.entity.CssPattern;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Css 模式 DTO
 *
 * @author YangRunkang(cruise)
 * @date 2020/10/02 21:21
 */
@Data
public class ListCssPatternDto extends BaseListDto {
    /**
     * Css模式集合
     */
    private List<CssPattern> patternList;

    private CssPattern userDefinedCss;

    public ListCssPatternDto(PageInfo pageInfo) {
        super(pageInfo);
        this.patternList = new ArrayList<>();
    }

    public ListCssPatternDto() {
        this.patternList = new ArrayList<>();
    }

    public List<CssPattern> getPatternList() {
        if (CollectionUtils.isEmpty(patternList)) {
            return new ArrayList<>();
        }
        return patternList;
    }

}
