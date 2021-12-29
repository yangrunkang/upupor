package com.upupor.service.business.ad;

import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.Member;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:48
 * @email: yangrunkang53@gmail.com
 */
public class MemberAd extends AbstractAd<Member> {

    public MemberAd(List<Member> members) {
        super(members);
    }

    @Override
    protected Boolean exists() {
        return getVoList().parallelStream().anyMatch(t -> t.getUserId().equals(CcConstant.GoogleAd.FEED_AD));
    }

    @Override
    protected void insertAd(int adIndex) {
        Member ad = new Member();
        ad.setUserId(CcConstant.GoogleAd.FEED_AD);
        getVoList().add(adIndex, ad);
    }
}
