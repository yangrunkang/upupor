package com.upupor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.MemberIntegral;
import com.upupor.service.dao.mapper.MemberIntegralMapper;
import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.service.service.MemberIntegralService;
import com.upupor.service.utils.CcUtils;
import com.upupor.spi.req.GetMemberIntegralReq;
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
                .eq(MemberIntegral::getIntegralRuleId, ruleId)
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
        addIntegral.setStatus(CcEnum.IntegralStatus.NORMAL.getStatus());
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
        reduceIntegral.setStatus(CcEnum.IntegralStatus.NORMAL.getStatus());
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
                .eq(MemberIntegral::getTargetId, getMemberIntegralReq.getTargetId())
                .eq(MemberIntegral::getStatus, getMemberIntegralReq.getStatus())
                .lt(MemberIntegral::getCreateTime, getMemberIntegralReq.getStartCreateTime())
                .gt(MemberIntegral::getCreateTime, getMemberIntegralReq.getEndCreateTime());
        return memberIntegralMapper.selectList(query);
    }

    @Override
    public void update(MemberIntegral memberIntegral) {
        memberIntegralMapper.updateById(memberIntegral);
    }
}
