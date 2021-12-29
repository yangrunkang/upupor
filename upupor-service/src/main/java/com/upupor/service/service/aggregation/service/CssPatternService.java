package com.upupor.service.service.aggregation.service;

import com.upupor.service.dao.entity.CssPattern;
import com.upupor.service.dto.page.common.ListCssPatternDto;

/**
 * CssPattern模式
 *
 * @author YangRunkang(cruise)
 * @date 2020/10/02 21:24
 */
public interface CssPatternService {


    ListCssPatternDto getAll(String userId);

    CssPattern getByUserId(String userId);

    Boolean add(CssPattern newCss);

    Boolean updateByUserId(CssPattern updateCss);

}