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

package com.upupor.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.data.dao.entity.Collect;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.CollectEnhance;
import com.upupor.data.dao.mapper.CollectMapper;
import com.upupor.data.dto.page.common.ListCollectDto;
import com.upupor.data.types.CollectType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.CollectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 收藏服务实现类
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 15:13
 */
@Service
@RequiredArgsConstructor
public class CollectServiceImpl implements CollectService {

    private final CollectMapper collectMapper;

    @Override
    public ListCollectDto listByUserIdManage(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> collectList = collectMapper.listByUserIdManage(userId);
        if (CollectionUtils.isEmpty(collectList)) {
            return new ListCollectDto();
        }
        PageInfo<Collect> pageInfo = new PageInfo<>(collectList);

        List<CollectEnhance> collectEnhances = Converter.collectEnhance(collectList);

        ListCollectDto listCollectDto = new ListCollectDto(pageInfo);
        listCollectDto.setCollectEnhanceList(collectEnhances);
        return listCollectDto;
    }

    @Override
    public Boolean existsCollectContent(String contentId, String userId) {
        if (CcUtils.isAllEmpty(contentId, userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        return collectMapper.existsCollectContent(contentId, userId) > 0;
    }

    @Override
    public Integer addCollect(Collect collect) {
        return collectMapper.insert(collect);
    }


    @Override
    public Integer collectNum(CollectType collectType, String collectValue) {
        return collectMapper.collectNum(collectType.getType(), collectValue);
    }

    @Override
    public Collect select(LambdaQueryWrapper<Collect> queryCollect) {
        return collectMapper.selectOne(queryCollect);
    }

    @Override
    public Integer update(Collect collect) {
        return collectMapper.updateById(collect);
    }
}
