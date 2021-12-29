package com.upupor.service.business.manage.business;

import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class FeedbackManage extends AbstractManageInfoGet {

    @Resource
    private MemberService memberService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {

        getMemberIndexDto().setMember(memberService.memberInfoData(manageDto.getUserId()));

    }
}
