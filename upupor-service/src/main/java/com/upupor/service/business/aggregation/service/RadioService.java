package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListRadioDto;

/**
 * 音频服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/15 20:31
 */
public interface RadioService {

    Boolean addRadio(Radio radio);

    ListRadioDto listRadioByUserId(Integer pageNum, Integer pageSize, String userId, String searchTitle);

    Radio getByRadioId(String radioId);

    Radio getByRadioIdAndUserId(String radioId, String userId);

    Integer updateRadio(Radio radio);

    ListRadioDto list(Integer pageNum, Integer pageSize);

    Integer total();

    /**
     * 文章作者是否有电台
     *
     * @param userId
     */
    Boolean userHasRadio(String userId);

}
