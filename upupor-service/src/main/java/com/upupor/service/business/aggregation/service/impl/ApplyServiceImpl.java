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
import com.upupor.service.business.aggregation.service.ApplyService;
import com.upupor.service.dao.entity.Apply;
import com.upupor.service.dao.entity.ApplyDocument;
import com.upupor.service.dao.mapper.ApplyDocumentMapper;
import com.upupor.service.dao.mapper.ApplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 申请服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 11:35
 */
@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final ApplyMapper applyMapper;

    private final ApplyDocumentMapper applyDocumentMapper;

    @Override
    public Integer addApply(Apply apply) {
        return applyMapper.insert(apply);
    }

    @Override
    public Integer total() {
        return applyMapper.total();
    }

    @Override
    public Apply getByApplyId(String applyId) {
        LambdaQueryWrapper<Apply> query = new LambdaQueryWrapper<Apply>()
                .eq(Apply::getApplyId, applyId);
        return applyMapper.selectOne(query);
    }

    @Override
    public Integer update(Apply apply) {
        return applyMapper.updateById(apply);
    }

    @Override
    public Integer commitDocument(ApplyDocument applyDocument) {
        return applyDocumentMapper.insert(applyDocument);
    }

}
