package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.BuriedPointData;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:57
 */
public interface BuriedPointDataService {

    /**
     * 添加埋点信息
     *
     * @param buriedPointData
     * @return
     */
    Integer addBuriedPointDataService(BuriedPointData buriedPointData);

    /**
     * 统计页面访问数
     *
     * @return
     */
    Integer countPageVisitNum(String pagePath);

}