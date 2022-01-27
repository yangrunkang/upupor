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

package com.upupor.service.business.aggregation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.business.aggregation.dao.entity.BusinessConfig;
import com.upupor.service.business.aggregation.dao.mapper.BusinessConfigMapper;
import com.upupor.service.types.BusinessConfigStatus;
import com.upupor.service.types.BusinessConfigType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月27日 23:53
 * @email: yangrunkang53@gmail.com
 */
@Service
@RequiredArgsConstructor
public class BusinessConfigService {

    private final BusinessConfigMapper businessConfigMapper;



    public List<BusinessConfig> listByBusinessConfigType(BusinessConfigType type){
        LambdaQueryWrapper<BusinessConfig> query = new LambdaQueryWrapper<BusinessConfig>()
                .eq(BusinessConfig::getType,type)
                .eq(BusinessConfig::getStatus, BusinessConfigStatus.NORMAL)
                ;

        return businessConfigMapper.selectList(query);

    }

}
