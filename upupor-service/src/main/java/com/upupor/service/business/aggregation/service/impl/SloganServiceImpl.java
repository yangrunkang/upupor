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

import com.upupor.service.business.aggregation.service.SloganService;
import com.upupor.service.dao.entity.Slogan;
import com.upupor.service.dao.mapper.SloganMapper;
import com.upupor.service.dao.mapper.SloganPathMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 标语实现层
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/23 22:53
 */
@Service
@RequiredArgsConstructor
public class SloganServiceImpl implements SloganService {

    private final SloganMapper sloganMapper;

    private final SloganPathMapper sloganPathMapper;

    @Override
    public List<Slogan> listByType(Integer type) {
        if (Objects.isNull(type)) {
            return new ArrayList<>();
        }
        return sloganMapper.listByType(type);
    }

    @Override
    public List<Slogan> listByPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return new ArrayList<>();
        }
        return sloganMapper.listByPath(path);
    }

    @Override
    public Boolean addSlogan(Slogan slogan) {
        if (Objects.isNull(slogan)) {
            return false;
        }
        return sloganMapper.insert(slogan) > 0;
    }

    @Override
    public Integer countSloganPathByPath(String servletPath) {
        return sloganPathMapper.countByPath(servletPath);
    }
}
