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
import com.upupor.data.dao.mapper.AttentionMapper;
import com.upupor.data.dao.mapper.FansMapper;
import com.upupor.data.dto.page.common.ListFansDto;
import com.upupor.service.base.FanService;
import com.upupor.service.base.MemberService;
import com.upupor.framework.utils.ServletUtils;
import com.upupor.service.outer.req.DelFanReq;
import com.upupor.data.types.FansStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 粉丝服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 14:50
 */
@Service
@RequiredArgsConstructor
public class FanServiceImpl implements FanService {

    private final FansMapper fansMapper;
    private final AttentionMapper attentionMapper;
    private final MemberService memberService;

    @Override
    public int add(Fans fans) {
        return fansMapper.insert(fans);
    }

    @Override
    public int getFansNum(String userId) {
        return fansMapper.getFansNum(userId);
    }

    @Override
    public ListFansDto getFans(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Fans> fans = fansMapper.getFans(userId);
        PageInfo<Fans> pageInfo = new PageInfo<>(fans);

        ListFansDto listFansDto = new ListFansDto(pageInfo);
        listFansDto.setFansList(pageInfo.getList());

        List<Fans> fansList = listFansDto.getFansList();
        if (!CollectionUtils.isEmpty(fansList)) {
            bindFansMemberInfo(fansList);
            listFansDto.setMemberList(fansList.stream().map(Fans::getMember).sorted(new MemberLastLoginTimeComparator()).collect(Collectors.toList()));
        }

        return listFansDto;
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

    @Override
    public Fans select(LambdaQueryWrapper<Fans> queryFans) {
        return fansMapper.selectOne(queryFans);
    }

    @Override
    public Integer delFans(DelFanReq delFanReq) {
        String fanId = delFanReq.getFanId();
        String userId = ServletUtils.getUserId();
        // 移除自己的粉丝
        LambdaQueryWrapper<Fans> query = new LambdaQueryWrapper<Fans>()
                .eq(Fans::getFanId, fanId)
                .eq(Fans::getUserId, userId)
                .eq(Fans::getFanStatus, FansStatus.NORMAL);

        Fans fans = select(query);
        int deleteFans = 0;
        if (Objects.nonNull(fans)) {
            deleteFans = fansMapper.deleteById(fans);
        }

        // 将对方的关注列表也删除了
        LambdaQueryWrapper<Attention> queryAttention = new LambdaQueryWrapper<Attention>()
                .eq(Attention::getUserId, fans.getFanUserId())
                .eq(Attention::getAttentionUserId, fans.getUserId())
                .eq(Attention::getAttentionStatus, FansStatus.NORMAL);
        Attention attention = attentionMapper.selectOne(queryAttention);
        int deleteAttention = 0;
        if (Objects.nonNull(attention)) {
            deleteAttention = attentionMapper.deleteById(attention);
        }


        return deleteFans + deleteAttention;
    }
}
