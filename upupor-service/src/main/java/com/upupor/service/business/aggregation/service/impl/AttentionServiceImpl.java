package com.upupor.service.business.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.service.AttentionService;
import com.upupor.service.business.aggregation.service.FanService;
import com.upupor.service.business.aggregation.service.MemberIntegralService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.Attention;
import com.upupor.service.dao.entity.Fans;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.mapper.AttentionMapper;
import com.upupor.service.dto.page.common.ListAttentionDto;
import com.upupor.service.listener.event.AttentionUserEvent;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddAttentionReq;
import com.upupor.spi.req.DelAttentionReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
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
    public Integer update(Attention attention) {
        return attentionMapper.updateById(attention);
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
                .eq(Attention::getAttentionStatus, CcEnum.AttentionStatus.NORMAL.getStatus());
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

        // 已关注
        if (addAttentionReq.getIsAttention()) {
            // 已关注的取消
            if (exists) {

                if (CcUtils.isAllEmpty(userId, addAttentionReq.getAttentionUserId())) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR);
                }
                // 删除已关注记录
                LambdaQueryWrapper<Attention> queryAttention = new LambdaQueryWrapper<Attention>()
                        .eq(Attention::getUserId, userId)
                        .eq(Attention::getAttentionUserId, addAttentionReq.getAttentionUserId());
                Attention attention = select(queryAttention);
                attention.setAttentionStatus(CcEnum.AttentionStatus.DELETED.getStatus());
                Integer updateAttention = update(attention);


                // 通知 被关注用户(AttentionUserId) 减少了一个粉丝
                LambdaQueryWrapper<Fans> queryFans = new LambdaQueryWrapper<Fans>()
                        .eq(Fans::getUserId, addAttentionReq.getAttentionUserId())
                        .eq(Fans::getFanUserId, userId);
                Fans fans = fanService.select(queryFans);
                fans.setFanStatus(CcEnum.FansStatus.DELETED.getStatus());
                Integer updateFan = fanService.update(fans);

                boolean handleSuccess = (updateAttention + updateFan) > 0;
                // 取消关注要将之前添加的积分添加回来
                if (handleSuccess) {
                    String attentionUserId = addAttentionReq.getAttentionUserId();
                    Member member = memberService.memberInfo(attentionUserId);
                    String userName = String.format(PROFILE_INTEGRAL, member.getUserId(), CcUtils.getUuId(), member.getUserName());
                    String text = "取消关注 " + userName + " ,扣减积分";
                    memberIntegralService.reduceIntegral(IntegralEnum.ATTENTION_AUTHOR, text, userId, member.getUserId());
                }

                return handleSuccess;
            }
        } else {
            // 未关注的,关注

            // 如果已经存在关注,则提示
            if (exists) {
                throw new BusinessException(ErrorCode.ATTENTION_REPEATED);
            }

            // 关注其他作者(这个是由用户独立管控的,不能删除)
            Attention attention = new Attention();
            attention.setAttentionId(CcUtils.getUuId());
            attention.setAttentionStatus(CcEnum.AttentionStatus.NORMAL.getStatus());
            attention.setUserId(userId);
            attention.setAttentionUserId(addAttentionReq.getAttentionUserId());
            attention.setCreateTime(CcDateUtil.getCurrentTime());
            attention.setSysUpdateTime(new Date());
            Integer addAttention = add(attention);

            // 通知 被关注用户(AttentionUserId) 涨了一个粉丝
            Fans fans = new Fans();
            fans.setFanId(CcUtils.getUuId());
            // 等他自己用自己的userId去查的时候,会有数据
            fans.setUserId(addAttentionReq.getAttentionUserId());
            fans.setFanUserId(userId);
            fans.setFanStatus(CcEnum.FansStatus.NORMAL.getStatus());
            fans.setCreateTime(CcDateUtil.getCurrentTime());
            fans.setSysUpdateTime(new Date());
            int addFans = fanService.add(fans);
            boolean handleSuccess = (addAttention + addFans) > 1;

            // 发送关注用户事件
            AttentionUserEvent attentionUserEvent = new AttentionUserEvent();
            attentionUserEvent.setAddAttentionReq(addAttentionReq);
            attentionUserEvent.setAttention(attention);
            eventPublisher.publishEvent(attentionUserEvent);

            // 添加积分
            if (handleSuccess) {
                String attentionUserId = addAttentionReq.getAttentionUserId();
                Member member = memberService.memberInfo(attentionUserId);
                String userName = String.format(PROFILE_INTEGRAL, member.getUserId(), CcUtils.getUuId(), member.getUserName());
                String text = "关注 " + userName + " ,增加积分";
                memberIntegralService.addIntegral(IntegralEnum.ATTENTION_AUTHOR, text, userId, fans.getFanId());
            }
            return handleSuccess;
        }
        return Boolean.FALSE;
    }


    @Override
    public Boolean delAttention(DelAttentionReq delAttentionReq) {
        String attentionId = delAttentionReq.getAttentionId();

        Attention attention = getAttentionByAttentionId(attentionId);

        if (Objects.isNull(attention)) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_EXISTS);
        }

        attention.setAttentionStatus(CcEnum.AttentionStatus.DELETED.getStatus());
        Integer updateAttentionCount = update(attention);

        // 对应的被关注的用户在其【粉丝列表】中要移除
        String userId = attention.getAttentionUserId();
        String fanUserId = attention.getUserId();

        LambdaQueryWrapper<Fans> queryFan = new LambdaQueryWrapper<Fans>()
                .eq(Fans::getUserId, userId)
                .eq(Fans::getFanUserId, fanUserId)
                .eq(Fans::getFanStatus, CcEnum.FansStatus.NORMAL.getStatus());
        Fans fans = fanService.select(queryFan);
        Integer updateFanCount = 0;
        if (Objects.nonNull(fans)) {
            fans.setFanStatus(CcEnum.FansStatus.DELETED.getStatus());
            updateFanCount = fanService.update(fans);
        }
        return (updateFanCount + updateAttentionCount) > 0;
    }
}
