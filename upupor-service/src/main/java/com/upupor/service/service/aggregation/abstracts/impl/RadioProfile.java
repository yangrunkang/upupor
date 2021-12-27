package com.upupor.service.service.aggregation.abstracts.impl;

import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.service.RadioService;
import com.upupor.service.service.aggregation.abstracts.ProfileAbstract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Random;

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
        // 个人主页电台列表添加广告
        if (!CollectionUtils.isEmpty(listRadioDto.getRadioList())) {
            boolean exists = listRadioDto.getRadioList().parallelStream().anyMatch(t -> t.getRadioId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = listRadioDto.getRadioList().size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Radio radio = new Radio();
                radio.setRadioId(CcConstant.GoogleAd.FEED_AD);
                radio.setUserId(CcConstant.GoogleAd.FEED_AD);
                listRadioDto.getRadioList().add(adIndex, radio);
            }
        }
    }
}
