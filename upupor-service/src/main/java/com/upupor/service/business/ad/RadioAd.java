package com.upupor.service.business.ad;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.Radio;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:52
 * @email: yangrunkang53@gmail.com
 */
public class RadioAd extends AbstractAd<Radio> {

    public RadioAd(List<Radio> radios) {
        super(radios);
    }

    @Override
    protected Boolean exists() {
        return getVoList().parallelStream().anyMatch(t -> t.getRadioId().equals(CcConstant.GoogleAd.FEED_AD));
    }

    @Override
    protected void insertAd(int adIndex) {
        Radio radio = new Radio();
        radio.setRadioId(CcConstant.GoogleAd.FEED_AD);
        radio.setUserId(CcConstant.GoogleAd.FEED_AD);
        radio.setCreateTime(CcDateUtil.getCurrentTime());
        getVoList().add(adIndex, radio);
    }
}
