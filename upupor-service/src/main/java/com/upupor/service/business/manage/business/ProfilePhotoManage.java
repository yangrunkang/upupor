package com.upupor.service.business.manage.business;

import com.upupor.service.business.aggregation.service.FileService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.dao.entity.Member;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class ProfilePhotoManage extends AbstractManageInfoGet {

    @Resource
    private MemberService memberService;

    @Resource
    private FileService fileService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        String userId = manageDto.getUserId();

        Member member = memberService.memberInfoData(userId);
        // 获取用户历史头像
        List<String> userHistoryViaList = fileService.getUserHistoryViaList(member.getUserId());
        if (!CollectionUtils.isEmpty(userHistoryViaList)) {
            member.setHistoryViaList(userHistoryViaList);
        }
        getMemberIndexDto().setMember(member);

    }
}