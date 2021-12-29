package com.upupor.service.business.aggregation.service.impl;

import com.upupor.service.business.aggregation.service.BuriedPointDataService;
import com.upupor.service.dao.entity.BuriedPointData;
import com.upupor.service.dao.mapper.BuriedPointDataMapper;
import com.upupor.service.dto.dao.PageVisitData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 埋点服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:56
 */
@Service
@RequiredArgsConstructor
public class BuriedPointDataServiceImpl implements BuriedPointDataService {

    private final BuriedPointDataMapper buriedPointDataMapper;

    @Override
    public Integer addBuriedPointDataService(BuriedPointData buriedPointData) {
        return buriedPointDataMapper.insert(buriedPointData);
    }

    @Override
    public Integer countPageVisitNum(String pagePath) {
        PageVisitData pageVisitData = buriedPointDataMapper.countPageVisitNum(pagePath);
        if (Objects.isNull(pageVisitData) || Objects.isNull(pageVisitData.getTotal())) {
            return BigDecimal.ZERO.intValue();
        }
        return pageVisitData.getTotal();
    }
}
