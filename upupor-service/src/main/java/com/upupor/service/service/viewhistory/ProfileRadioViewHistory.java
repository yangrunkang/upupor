package com.upupor.service.service.viewhistory;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.ViewHistory;
import org.springframework.stereotype.Component;

/**
 * @author cruise
 * @createTime 2021-12-28 18:49
 */
@Component
public class ProfileRadioViewHistory extends ProfileAttentionViewHistory {

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_RADIO;
    }
    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Member member : getTargetList()) {
                if (member.getUserId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(member.getUserName()+"的电台");
                    viewHistory.setUrl("/profile/" + member.getUserId() + "/radio");
                    viewHistory.setSource(viewTargetType().getName());
                }
            }
        }
    }

}
