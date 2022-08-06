/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.data.dao.entity.MemberIntegral;
import com.upupor.service.data.dao.mapper.MemberIntegralMapper;
import com.upupor.service.data.service.MemberIntegralService;
import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.types.MemberIntegralStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户积分实现
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/08 09:35
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberIntegralServiceImpl implements MemberIntegralService {

    private final MemberIntegralMapper memberIntegralMapper;

    @Override
    public Boolean checkExists(GetMemberIntegralReq memberIntegralReq) {
        Integer count = memberIntegralMapper.countByCondition(memberIntegralReq);
        if (Objects.isNull(count)) {
            return Boolean.FALSE;
        }

        if (count > 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean addMemberIntegral(MemberIntegral memberIntegral) {
        try {
            return memberIntegralMapper.insert(memberIntegral) > 0;
        } catch (Exception e) {
            log.error("插入积分数据失败,积分:{},异常:{}", memberIntegral, e.getMessage());
            return false;
        }
    }

    @Override
    public Integer getUserIntegral(String userId) {
        Integer totalUserIntegral = memberIntegralMapper.getTotalUserIntegral(userId);
        if (Objects.isNull(totalUserIntegral)) {
            totalUserIntegral = 0;
        }
        return totalUserIntegral;
    }

    @Override
    public ListIntegralDto list(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<MemberIntegral> query = new LambdaQueryWrapper<MemberIntegral>()
                .eq(MemberIntegral::getIntegralUserId, userId)
                .orderByDesc(MemberIntegral::getCreateTime);
        List<MemberIntegral> memberIntegralList = memberIntegralMapper.selectList(query);
        PageInfo<MemberIntegral> pageInfo = new PageInfo<>(memberIntegralList);

        ListIntegralDto listIntegralDto = new ListIntegralDto(pageInfo);
        listIntegralDto.setMemberIntegralList(pageInfo.getList());
        return listIntegralDto;
    }

    @Override
    public ListIntegralDto list(String userId, Integer ruleId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        LambdaQueryWrapper<MemberIntegral> query = new LambdaQueryWrapper<MemberIntegral>()
                .eq(Objects.nonNull(ruleId), MemberIntegral::getIntegralRuleId, ruleId)
                .eq(MemberIntegral::getIntegralUserId, userId)
                .orderByDesc(MemberIntegral::getCreateTime);
        List<MemberIntegral> memberIntegralList = memberIntegralMapper.selectList(query);

        PageInfo<MemberIntegral> pageInfo = new PageInfo<>(memberIntegralList);
        ListIntegralDto listIntegralDto = new ListIntegralDto(pageInfo);
        listIntegralDto.setMemberIntegralList(pageInfo.getList());

        return listIntegralDto;
    }


    @Override
    public boolean addIntegral(IntegralEnum integralEnum, String desc, String userId, String targetId) {
        MemberIntegral addIntegral = new MemberIntegral();
        addIntegral.setIntegralId(CcUtils.getUuId());
        addIntegral.setIntegralUserId(userId);
        addIntegral.setIntegralRuleId(integralEnum.getRuleId().longValue());
        addIntegral.setIntegralValue(integralEnum.getIntegral().longValue());
        addIntegral.setIntegralText(desc);
        if (StringUtils.isEmpty(desc)) {
            addIntegral.setIntegralText(integralEnum.getRuleDesc());
        }
        addIntegral.setStatus(MemberIntegralStatus.NORMAL);
        addIntegral.setSysUpdateTime(new Date());
        addIntegral.setCreateTime(CcDateUtil.getCurrentTime());
        addIntegral.setTargetId(targetId);
        return addMemberIntegral(addIntegral);
    }

    @Override
    public boolean addIntegral(IntegralEnum integralEnum, String userId, String targetId) {
        return addIntegral(integralEnum, integralEnum.getRuleDesc(), userId, targetId);
    }

    @Override
    public boolean reduceIntegral(IntegralEnum integralEnum, String desc, String userId, String targetId) {
        MemberIntegral reduceIntegral = new MemberIntegral();
        reduceIntegral.setIntegralId(CcUtils.getUuId());
        reduceIntegral.setIntegralUserId(userId);
        reduceIntegral.setIntegralRuleId(integralEnum.getRuleId().longValue());
        reduceIntegral.setIntegralValue(-integralEnum.getIntegral().longValue());
        reduceIntegral.setIntegralText(desc);
        if (StringUtils.isEmpty(desc)) {
            reduceIntegral.setIntegralText(integralEnum.getRuleDesc());
        }
        reduceIntegral.setStatus(MemberIntegralStatus.NORMAL);
        reduceIntegral.setSysUpdateTime(new Date());
        reduceIntegral.setCreateTime(CcDateUtil.getCurrentTime());
        reduceIntegral.setTargetId(targetId);
        return addMemberIntegral(reduceIntegral);
    }

    @Override
    public List<MemberIntegral> getByGetMemberIntegralReq(GetMemberIntegralReq getMemberIntegralReq) {
        LambdaQueryWrapper<MemberIntegral> query = new LambdaQueryWrapper<MemberIntegral>()
                .eq(MemberIntegral::getIntegralUserId, getMemberIntegralReq.getUserId())
                .eq(MemberIntegral::getIntegralRuleId, getMemberIntegralReq.getRuleId())
                .eq(MemberIntegral::getTargetId, getMemberIntegralReq.getTargetId());

        if (Objects.nonNull(getMemberIntegralReq.getStatus())) {
            query.eq(MemberIntegral::getStatus, getMemberIntegralReq.getStatus());
        }
        if (Objects.nonNull(getMemberIntegralReq.getStartCreateTime())) {
            query.lt(MemberIntegral::getCreateTime, getMemberIntegralReq.getStartCreateTime());
        }
        if (Objects.nonNull(getMemberIntegralReq.getEndCreateTime())) {
            query.gt(MemberIntegral::getCreateTime, getMemberIntegralReq.getEndCreateTime());
        }
        return memberIntegralMapper.selectList(query);
    }

    @Override
    public void update(MemberIntegral memberIntegral) {
        memberIntegralMapper.updateById(memberIntegral);
    }
}
