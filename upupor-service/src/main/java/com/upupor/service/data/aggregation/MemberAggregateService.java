/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.data.aggregation;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.MemberExtend;
import com.upupor.service.data.aggregation.service.MemberExtendService;
import com.upupor.service.data.aggregation.service.MemberIntegralService;
import com.upupor.service.data.aggregation.service.MemberService;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.dto.page.MemberListDto;
import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.service.dto.page.common.ListMemberDto;
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

        AbstractAd.ad(listMemberDto.getMemberList());

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
