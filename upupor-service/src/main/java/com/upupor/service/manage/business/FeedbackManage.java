package com.upupor.service.manage.business;

import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.aggregation.service.MemberService;
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
