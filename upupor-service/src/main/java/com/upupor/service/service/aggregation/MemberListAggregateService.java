package com.upupor.service.service.aggregation;

import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.MemberExtend;
import com.upupor.service.dto.page.MemberListDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.service.MemberExtendService;
import com.upupor.service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户列表聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/29 15:23
 */
@Service
@RequiredArgsConstructor
public class MemberListAggregateService {

    private final MemberService memberService;

    private final MemberExtendService memberExtendService;

    public MemberListDto userList(Integer pageNum, Integer pageSize) {

        // 获取用户列表
        ListMemberDto listMemberDto = memberService.list(pageNum, pageSize);

        // 完整用户拓展信息
        List<String> userIdList = listMemberDto.getMemberList().stream()
                .map(Member::getUserId).distinct().collect(Collectors.toList());
        List<MemberExtend> memberExtends = memberExtendService.extendInfoByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(memberExtends)) {
            return new MemberListDto();
        }

        listMemberDto.getMemberList().forEach(member -> {
            for (MemberExtend memberExtend : memberExtends) {
                if (member.getUserId().equals(memberExtend.getUserId())) {
                    member.setMemberExtend(memberExtend);
                }
            }
        });

        // 封装返回对象
        MemberListDto memberListDto = new MemberListDto();
        memberListDto.setListMemberDto(listMemberDto);
        return memberListDto;
    }

}
