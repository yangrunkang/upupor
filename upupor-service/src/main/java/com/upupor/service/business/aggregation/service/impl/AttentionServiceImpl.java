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

package com.upupor.service.business.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.business.aggregation.service.AttentionService;
import com.upupor.service.business.aggregation.service.FanService;
import com.upupor.service.business.aggregation.service.MemberIntegralService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.Attention;
import com.upupor.service.dao.entity.Fans;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.mapper.AttentionMapper;
import com.upupor.service.dao.mapper.FansMapper;
import com.upupor.service.dto.page.common.ListAttentionDto;
import com.upupor.service.listener.event.AttentionUserEvent;
import com.upupor.service.spi.req.AddAttentionReq;
import com.upupor.service.spi.req.DelAttentionReq;
import com.upupor.service.types.AttentionStatus;
import com.upupor.service.types.FansStatus;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.upupor.service.common.CcConstant.MsgTemplate.PROFILE_INTEGRAL;

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
    public Boolean checkExists(String attentionUserId, String userId) {
        return attentionMapper.checkExists(attentionUserId, userId) > 0;
    }


    @Override
    public ListAttentionDto getAttentions(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Attention> fans = attentionMapper.getAttentions(userId);
        PageInfo<Attention> pageInfo = new PageInfo<>(fans);
        ListAttentionDto listAttentionDto = new ListAttentionDto(pageInfo);
        listAttentionDto.setAttentionList(pageInfo.getList());

        List<Attention> attentionList = listAttentionDto.getAttentionList();
        if (!CollectionUtils.isEmpty(attentionList)) {
            bindAttentionMemberInfo(attentionList);
            listAttentionDto.setMemberList(attentionList.stream().map(Attention::getMember).collect(Collectors.toList()));
        }
        return listAttentionDto;
    }

    /**
     * 封装关注者 粉丝信息
     *
     * @param attentionList
     */
    private void bindAttentionMemberInfo(List<Attention> attentionList) {
        List<String> attentionUserIdList = attentionList.stream().map(Attention::getAttentionUserId).distinct().collect(Collectors.toList());
        List<Member> memberList = memberService.listByUserIdList(attentionUserIdList);

        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (Attention attention : attentionList) {
            for (Member member : memberList) {
                if (attention.getAttentionUserId().equals(member.getUserId())) {
                    attention.setMember(member);
                }
            }
        }
    }

    @Override
    public Integer getAttentionNum(String userId) {
        return attentionMapper.getAttentionNum(userId);
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

        String userId = ServletUtils.getUserId();
        if (addAttentionReq.getAttentionUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ATTENTION_SELF);
        }
        // 检查是否存在
        Boolean exists = checkExists(addAttentionReq.getAttentionUserId(), userId);
        if (exists) {
            return cancelAttention(addAttentionReq, userId);
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
            Member member = memberService.memberInfo(attentionUserId);
            String userName = String.format(PROFILE_INTEGRAL, member.getUserId(), CcUtils.getUuId(), member.getUserName());
            String text = "关注 " + userName + " ,增加积分";
            memberIntegralService.addIntegral(IntegralEnum.ATTENTION_AUTHOR, text, userId, fans.getFanId());
        }
        return handleSuccess;
    }

    private boolean cancelAttention(AddAttentionReq addAttentionReq, String userId) {
        if (CcUtils.isAllEmpty(userId, addAttentionReq.getAttentionUserId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 删除关注记录
        LambdaQueryWrapper<Attention> queryAttention = new LambdaQueryWrapper<Attention>()
                .eq(Attention::getUserId, userId)
                .eq(Attention::getAttentionUserId, addAttentionReq.getAttentionUserId());
        Attention attention = select(queryAttention);
        int deleteAttention = attentionMapper.deleteById(attention);

        // 将对方的粉丝也删除
        LambdaQueryWrapper<Fans> queryFans = new LambdaQueryWrapper<Fans>()
                .eq(Fans::getUserId, addAttentionReq.getAttentionUserId())
                .eq(Fans::getFanUserId, userId);
        Fans fans = fanService.select(queryFans);
        int deleteFan = fansMapper.deleteById(fans);

        boolean isDeleted = (deleteAttention + deleteFan) > 0;
        // 取消关注要将之前添加的积分添加回来
        if (isDeleted) {
            String attentionUserId = addAttentionReq.getAttentionUserId();
            Member member = memberService.memberInfo(attentionUserId);
            String userName = String.format(PROFILE_INTEGRAL, member.getUserId(), CcUtils.getUuId(), member.getUserName());
            String text = "取消关注 " + userName + " ,扣减积分";
            memberIntegralService.reduceIntegral(IntegralEnum.ATTENTION_AUTHOR, text, userId, member.getUserId());
        }

        return isDeleted;
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
        return (deleteFans + deleteAttention) > 0;
    }
}
