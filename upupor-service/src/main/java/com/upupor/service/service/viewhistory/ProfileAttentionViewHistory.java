package com.upupor.service.service.viewhistory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.dao.mapper.MemberMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-28 18:50
 */
@Component
public class ProfileAttentionViewHistory extends AbstractViewHistory<Member> {

    @Resource
    private MemberMapper memberMapper;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_ATTENTION;
    }

    @Override
    public List<Member> getTargetList() {
        LambdaQueryWrapper<Member> query = new LambdaQueryWrapper<Member>()
                .in(Member::getUserId, getViewHistoryTargetIdList());
        return memberMapper.selectList(query);
    }

    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Member member : getTargetList()) {
                if (member.getUserId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(member.getUserName()+"的关注");
                    viewHistory.setUrl("/profile/" + member.getUserId() + "/attention");
                }
            }
        }
    }


}
