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

package com.upupor.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.data.dao.entity.Attention;
import com.upupor.data.dao.entity.Fans;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.comparator.MemberLastLoginTimeComparator;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.AttentionEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dao.mapper.AttentionMapper;
import com.upupor.data.dao.mapper.FansMapper;
import com.upupor.data.dto.page.common.ListAttentionDto;
import com.upupor.data.types.AttentionStatus;
import com.upupor.data.types.FansStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.utils.JwtUtils;
import com.upupor.service.base.AttentionService;
import com.upupor.service.base.FanService;
import com.upupor.service.base.MemberIntegralService;
import com.upupor.service.base.MemberService;
import com.upupor.service.listener.event.AttentionUserEvent;
import com.upupor.service.outer.req.AddAttentionReq;
import com.upupor.service.outer.req.DelAttentionReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.upupor.framework.CcConstant.MsgTemplate.PROFILE_INTEGRAL;

/**
 * 关注服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 21:19
 */
@Service
@RequiredArgsConstructor
public class AttentionServiceImpl implements AttentionService {

    private final AttentionMapper attentionMapper;
    private final FansMapper fansMapper;
    private final MemberIntegralService memberIntegralService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;
    private final FanService fanService;

    @Override
    public Integer add(Attention attention) {
        return attentionMapper.insert(attention);
    }


    @Override
    public ListAttentionDto getAttentions(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Attention> attentions = attentionMapper.getAttentions(userId);
        if (CollectionUtils.isEmpty(attentions)) {
            return new ListAttentionDto();
        }
        PageInfo<Attention> pageInfo = new PageInfo<>(attentions);
        ListAttentionDto listAttentionDto = new ListAttentionDto(pageInfo);

        List<AttentionEnhance> attentionList = Converter.attentionEnhance(attentions);
        listAttentionDto.setAttentionEnhanceList(attentionList);

        if (!CollectionUtils.isEmpty(attentionList)) {
            bindAttentionMemberInfo(attentionList);
            List<MemberEnhance> memberEnhanceList = attentionList.stream().map(AttentionEnhance::getMemberEnhance).collect(Collectors.toList());
            memberService.bindMemberExtendEnhance(memberEnhanceList);
            listAttentionDto.setMemberEnhanceList(memberEnhanceList.stream().sorted(new MemberLastLoginTimeComparator()).collect(Collectors.toList()));
        }
        return listAttentionDto;
    }

    /**
     * 封装关注者 粉丝信息
     *
     * @param attentionList
     */
    private void bindAttentionMemberInfo(List<AttentionEnhance> attentionList) {
        List<String> attentionUserIdList = attentionList.stream()
                .map(AttentionEnhance::getAttention)
                .map(Attention::getAttentionUserId)
                .distinct().collect(Collectors.toList());
        List<MemberEnhance> memberList = memberService.listByUserIdList(attentionUserIdList);

        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (AttentionEnhance attentionEnhance : attentionList) {
            for (MemberEnhance memberEnhance : memberList) {
                if (attentionEnhance.getAttention().getAttentionUserId().equals(memberEnhance.getMember().getUserId())) {
                    attentionEnhance.setMemberEnhance(memberEnhance);
                }
            }
        }
    }

    @Override
    public Integer getAttentionNum(String userId) {
        return attentionMapper.getAttentionNum(userId);
    }

    @Override
    public Attention getAttention(String attentionUserId, String userId) {
        LambdaQueryWrapper<Attention> queryAttention = new LambdaQueryWrapper<Attention>()
                .eq(Attention::getUserId, userId)
                .eq(Attention::getAttentionUserId, attentionUserId)
                .eq(Attention::getAttentionStatus, FansStatus.NORMAL);
        return attentionMapper.selectOne(queryAttention);

    }

    @Override
    public Attention getAttentionByAttentionId(String attentionId) {
        if (CcUtils.isAllEmpty(attentionId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<Attention> attention = new LambdaQueryWrapper<Attention>()
                .eq(Attention::getAttentionId, attentionId)
                .eq(Attention::getAttentionStatus, AttentionStatus.NORMAL);
        return select(attention);
    }

    @Override
    public Attention select(LambdaQueryWrapper<Attention> queryAttention) {
        return attentionMapper.selectOne(queryAttention);
    }

    @Override
    public Boolean attention(AddAttentionReq addAttentionReq) {
        if (StringUtils.isEmpty(addAttentionReq.getAttentionUserId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "关注者的用户id为空");
        }

        String userId = JwtUtils.getUserId();
        if (addAttentionReq.getAttentionUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ATTENTION_SELF);
        }
        Attention attention = getAttention(addAttentionReq.getAttentionUserId(), userId);
        if (Objects.nonNull(attention)) {
            DelAttentionReq delAttentionReq = new DelAttentionReq();
            delAttentionReq.setAttentionId(attention.getAttentionId());
            return delAttention(delAttentionReq);
        } else {
            return toAttention(addAttentionReq, userId);
        }
    }

    private boolean toAttention(AddAttentionReq addAttentionReq, String userId) {
        // 添加关注记录
        Attention attention = Attention.init();
        attention.setUserId(userId);
        attention.setAttentionUserId(addAttentionReq.getAttentionUserId());
        Integer addAttention = add(attention);

        // 给对方添加一个粉丝记录
        Fans fans = Fans.init();
        fans.setUserId(addAttentionReq.getAttentionUserId());
        fans.setFanUserId(userId);
        int addFans = fanService.add(fans);
        boolean handleSuccess = (addAttention + addFans) > 1;


        if (handleSuccess) {
            // 发送关注用户事件
            AttentionUserEvent attentionUserEvent = new AttentionUserEvent();
            attentionUserEvent.setAddAttentionReq(addAttentionReq);
            attentionUserEvent.setAttention(attention);
            eventPublisher.publishEvent(attentionUserEvent);

            // 添加积分
            String attentionUserId = addAttentionReq.getAttentionUserId();
            MemberEnhance memberEnhance = memberService.memberInfo(attentionUserId);
            Member member = memberEnhance.getMember();
            String userName = String.format(PROFILE_INTEGRAL, member.getUserId(), CcUtils.getUuId(), member.getUserName());
            String text = "关注 " + userName + " ,增加积分";
            memberIntegralService.addIntegral(IntegralEnum.ATTENTION_AUTHOR, text, userId, fans.getFanId());
        }
        return handleSuccess;
    }


    @Override
    public Boolean delAttention(DelAttentionReq delAttentionReq) {
        Attention attention = getAttentionByAttentionId(delAttentionReq.getAttentionId());
        int deleteAttention = 0;
        if (Objects.nonNull(attention)) {
            deleteAttention = attentionMapper.deleteById(attention);
        }


        // 对应的被关注的用户在其【粉丝列表】中要移除
        String userId = attention.getAttentionUserId();
        String fanUserId = attention.getUserId();
        LambdaQueryWrapper<Fans> queryFan = new LambdaQueryWrapper<Fans>()
                .eq(Fans::getUserId, userId)
                .eq(Fans::getFanUserId, fanUserId)
                .eq(Fans::getFanStatus, FansStatus.NORMAL.getStatus());
        Fans fans = fanService.select(queryFan);
        int deleteFans = 0;
        if (Objects.nonNull(fans)) {
            deleteFans = fansMapper.deleteById(fans);
        }
        boolean delAttention = (deleteFans + deleteAttention) > 0;
        if (delAttention) {
            String attentionUserId = attention.getUserId();
            MemberEnhance memberEnhance = memberService.memberInfo(attentionUserId);
            Member member = memberEnhance.getMember();
            String userName = String.format(PROFILE_INTEGRAL, member.getUserId(), CcUtils.getUuId(), member.getUserName());
            String text = "取消关注 " + userName + " ,扣减积分";
            memberIntegralService.reduceIntegral(IntegralEnum.ATTENTION_AUTHOR, text, member.getUserId(), attention.getAttentionUserId());
        }
        return delAttention;
    }
}
