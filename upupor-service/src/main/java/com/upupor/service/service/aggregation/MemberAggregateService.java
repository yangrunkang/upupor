package com.upupor.service.service.aggregation;

import com.upupor.service.business.AdService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.MemberExtend;
import com.upupor.service.dto.page.MemberListDto;
import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.service.MemberExtendService;
import com.upupor.service.service.MemberIntegralService;
import com.upupor.service.service.MemberService;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 23:54
 */
@Service
@RequiredArgsConstructor
public class MemberAggregateService {

    private final MemberIntegralService memberIntegralService;
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

        AdService.memberListAd(listMemberDto.getMemberList());

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

    public ListIntegralDto integralRecord(Integer ruleId, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(ruleId)) {
            throw new BusinessException(ErrorCode.RULE_ID_NULL);
        }
        String userId = getUserId();
        ListIntegralDto listIntegralDto = memberIntegralService.list(userId, ruleId, pageNum, pageSize);
        if (Objects.isNull(listIntegralDto)) {
            listIntegralDto = new ListIntegralDto();
        }
        // 计算总积分
        listIntegralDto.setTotalIntegral(memberIntegralService.getUserIntegral(userId));
        return listIntegralDto;
    }

    private String getUserId() {
        String userId = ServletUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        return userId;
    }
}
