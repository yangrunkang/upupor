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

package com.upupor.service.business.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.business.aggregation.dao.entity.Apply;
import com.upupor.service.business.aggregation.dao.mapper.ApplyMapper;
import com.upupor.service.business.manage.service.ApplyManageService;
import com.upupor.service.dto.page.common.ListApplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 申请管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:37
 */
@Service
@RequiredArgsConstructor
public class ApplyManageServiceImpl implements ApplyManageService {
    private final ApplyMapper applyMapper;

    @Override
    public ListApplyDto listApplyListByUserIdManage(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Apply> applyList = applyMapper.listApplyListByUserIdManage(userId);
        PageInfo<Apply> pageInfo = new PageInfo<>(applyList);

        ListApplyDto listApplyDto = new ListApplyDto(pageInfo);
        listApplyDto.setApplyList(applyList);

        return listApplyDto;
    }
}
