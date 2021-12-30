package com.upupor.service.business.profile;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.business.aggregation.service.FanService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.common.ListFansDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:01
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class FansProfile extends ProfileAbstract {
    private final FanService fanService;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_FANS;
    }

    @Override
    protected void setSpecifyData(String userId, Integer pageNum, Integer pageSize) {
        ListFansDto listFansDto = fanService.getFans(userId, pageNum, pageSize);
        getMemberIndexDto().setListFansDto(listFansDto);
    }

    @Override
    protected void addAd() {
        ListFansDto listFansDto = getMemberIndexDto().getListFansDto();
        AbstractAd.ad(listFansDto.getMemberList());
    }
}