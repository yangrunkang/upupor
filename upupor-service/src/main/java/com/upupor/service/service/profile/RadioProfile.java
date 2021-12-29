package com.upupor.service.service.profile;

import com.upupor.service.business.AdService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.service.aggregation.service.RadioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:01
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class RadioProfile extends ProfileAbstract {
    private final RadioService radioService;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_RADIO;
    }

    @Override
    protected void setSpecifyData(String userId, Integer pageNum, Integer pageSize) {
        ListRadioDto listRadioDto = radioService.listRadioByUserId(pageNum, pageSize, userId, null);
        getMemberIndexDto().setListRadioDto(listRadioDto);

    }

    @Override
    protected void addAd() {
        ListRadioDto listRadioDto = getMemberIndexDto().getListRadioDto();
        AdService.radioListAd(listRadioDto.getRadioList());
    }
}
