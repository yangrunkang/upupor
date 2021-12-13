package com.upupor.service.dto.page;

import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListRadioDto;
import lombok.Data;

/**
 * 电台页面数据Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/15 23:08
 */
@Data
public class RadioIndexDto {

    /**
     * 电台
     */
    private Radio radio;

    private ListRadioDto listRadioDto;

}
