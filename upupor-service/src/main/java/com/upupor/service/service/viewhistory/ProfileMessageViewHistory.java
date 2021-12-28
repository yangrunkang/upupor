package com.upupor.service.service.viewhistory;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.dao.mapper.CommentMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-28 18:49
 */
@Component
public class ProfileMessageViewHistory extends ProfileAttentionViewHistory{
    @Resource
    private CommentMapper commentMapper;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_MESSAGE;
    }
    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Member member : getTargetList()) {
                if (member.getUserId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(member.getUserName()+"的留言板");
                    viewHistory.setUrl("/profile-message/" + member.getUserId());
                }
            }
        }
    }


}
