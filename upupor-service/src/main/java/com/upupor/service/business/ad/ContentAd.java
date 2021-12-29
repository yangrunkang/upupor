package com.upupor.service.business.ad;

import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.Content;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:51
 * @email: yangrunkang53@gmail.com
 */
public class ContentAd extends AbstractAd<Content>{
    public ContentAd(List<Content> contents) {
        super(contents);
    }

    @Override
    protected Boolean exists() {
        return getVoList().parallelStream().anyMatch(t -> t.getContentId().equals(CcConstant.GoogleAd.FEED_AD));
    }

    @Override
    protected void insertAd(int adIndex) {
        Content adContent = new Content();
        adContent.setContentId(CcConstant.GoogleAd.FEED_AD);
        getVoList().add(adIndex, adContent);
    }
}
