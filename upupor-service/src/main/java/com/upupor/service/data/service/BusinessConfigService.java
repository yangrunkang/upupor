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

package com.upupor.service.data.aggregation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.data.dao.entity.BusinessConfig;
import com.upupor.service.data.dao.mapper.BusinessConfigMapper;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.dto.page.common.ListCssPatternDto;
import com.upupor.service.types.BusinessConfigStatus;
import com.upupor.service.types.BusinessConfigType;
import org.apache.logging.log4j.util.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月27日 23:53
 * @email: yangrunkang53@gmail.com
 */
@Service
@RequiredArgsConstructor
public class BusinessConfigService {

    private final BusinessConfigMapper businessConfigMapper;


    public List<BusinessConfig> listByBusinessConfigType(BusinessConfigType type) {
        LambdaQueryWrapper<BusinessConfig> query = new LambdaQueryWrapper<BusinessConfig>()
                .eq(BusinessConfig::getType, type)
                .eq(BusinessConfig::getStatus, BusinessConfigStatus.NORMAL)
                .isNull(BusinessConfig::getUserId)
                ;
        return businessConfigMapper.selectList(query);
    }


    public BusinessConfig getUserDefinedBgStyle(String userId) {
        LambdaQueryWrapper<BusinessConfig> query = new LambdaQueryWrapper<BusinessConfig>()
                .eq(BusinessConfig::getType, BusinessConfigType.CSS_BG)
                .eq(BusinessConfig::getUserId, userId)
                .eq(BusinessConfig::getStatus, BusinessConfigStatus.NORMAL);
        List<BusinessConfig> businessConfigList = businessConfigMapper.selectList(query);

        if(CollectionUtils.isEmpty(businessConfigList)){
            return null;
        }

        if (businessConfigList.size() > 1) {
            throw new BusinessException(ErrorCode.CSS_BG_CONFIG_MORE);
        }

        return businessConfigList.get(0);
    }

    public Boolean add(BusinessConfig entity) {
        return businessConfigMapper.insert(entity) > 0;
    }

    public Boolean update(BusinessConfig entity) {
        return businessConfigMapper.updateById(entity) > 0;
    }


    public ListCssPatternDto getAll(String userId) {
        List<BusinessConfig> cssPatternList = listByBusinessConfigType(BusinessConfigType.CSS_BG);
        if (CollectionUtils.isEmpty(cssPatternList)) {
            return new ListCssPatternDto();
        }

        ListCssPatternDto listCssPatternDto = new ListCssPatternDto();
        listCssPatternDto.setPatternList(cssPatternList);
        listCssPatternDto.setTotal((long) cssPatternList.size());
        listCssPatternDto.setPaginationHtml(Strings.EMPTY);

        if (!StringUtils.isEmpty(userId)) {
            BusinessConfig userDefinedBgStyle = getUserDefinedBgStyle(userId);
            if(Objects.nonNull(userDefinedBgStyle)){
                listCssPatternDto.setUserDefinedCss(userDefinedBgStyle);
            }
        }

        return listCssPatternDto;
    }
}
