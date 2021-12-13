package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.BuriedPointData;
import com.upupor.service.dto.dao.PageVisitData;
import org.apache.ibatis.annotations.Param;

public interface BuriedPointDataMapper extends BaseMapper<BuriedPointData> {

    /**
     * 统计页面访问数
     *
     * @param pagePath
     * @return
     */
    PageVisitData countPageVisitNum(@Param("pagePath") String pagePath);
}
