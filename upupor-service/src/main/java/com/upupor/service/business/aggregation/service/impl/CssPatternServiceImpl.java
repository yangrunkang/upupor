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

package com.upupor.service.business.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.business.aggregation.service.CssPatternService;
import com.upupor.service.dao.entity.CssPattern;
import com.upupor.service.dao.mapper.CssPatternMapper;
import com.upupor.service.dto.page.common.ListCssPatternDto;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author YangRunkang(cruise)
 * @date 2020/10/02 21:25
 */
@Service
@RequiredArgsConstructor
public class CssPatternServiceImpl implements CssPatternService {

    private final CssPatternMapper cssPatternMapper;

    @Override
    public ListCssPatternDto getAll(String userId) {

        List<CssPattern> cssPatternList = cssPatternMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtils.isEmpty(cssPatternList)) {
            cssPatternList = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(cssPatternList)) {
            return new ListCssPatternDto();
        }

        ListCssPatternDto listCssPatternDto = new ListCssPatternDto();
        listCssPatternDto.setPatternList(cssPatternList);
        listCssPatternDto.setTotal((long) cssPatternList.size());
        listCssPatternDto.setPaginationHtml(Strings.EMPTY);

        if (!StringUtils.isEmpty(userId)) {
            CssPattern userCssPattern = getByUserId(userId);
            if (Objects.nonNull(userCssPattern)) {
                listCssPatternDto.setUserDefinedCss(userCssPattern);
            }
        }

        return listCssPatternDto;
    }

    @Override
    public CssPattern getByUserId(String userId) {
        LambdaQueryWrapper<CssPattern> query = new LambdaQueryWrapper<CssPattern>()
                .eq(CssPattern::getUserId, userId);
        return cssPatternMapper.selectOne(query);
    }

    @Override
    public Boolean add(CssPattern newCss) {
        return cssPatternMapper.insert(newCss) > 0;
    }

    @Override
    public Boolean updateByUserId(CssPattern updateCss) {
        return cssPatternMapper.updateById(updateCss) > 0;
    }
}
