package com.upupor.service.manage.business;

import com.upupor.service.dao.entity.Fans;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.page.common.ListFansDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.FanService;
import com.upupor.service.service.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class FansManage extends AbstractManageInfoGet {
    @Resource
    private FanService fanService;
    @Resource
    private MemberService memberService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        String userId = manageDto.getUserId();
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();


        ListFansDto listFansDto = fanService.getFans(userId, pageNum, pageSize);
        List<Fans> fansList = listFansDto.getFansList();

        //
        if (!CollectionUtils.isEmpty(fansList)) {
            bindFansMemberInfo(fansList);
        }

        getMemberIndexDto().setListFansDto(listFansDto);

    }

    private void bindFansMemberInfo(List<Fans> fansList) {
        List<String> fanUserIdList = fansList.stream().map(Fans::getFanUserId).distinct().collect(Collectors.toList());
        List<Member> memberList = memberService.listByUserIdList(fanUserIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (Fans fans : fansList) {
            for (Member member : memberList) {
                if (fans.getFanUserId().equals(member.getUserId())) {
                    fans.setMember(member);
                }
            }
        }
    }


}
