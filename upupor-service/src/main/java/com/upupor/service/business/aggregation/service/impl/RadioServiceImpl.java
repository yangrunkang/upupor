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
import com.google.common.collect.Lists;
import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.dao.entity.Radio;
import com.upupor.service.business.aggregation.dao.mapper.RadioMapper;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.RadioService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.types.RadioStatus;
import com.upupor.service.utils.Asserts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author YangRunkang(cruise)
 * @date 2020/11/15 20:34
 */
@Service
@RequiredArgsConstructor
public class RadioServiceImpl implements RadioService {

    private final RadioMapper radioMapper;

    private final MemberService memberService;

    private final ContentService contentService;

    @Override
    public Boolean addRadio(Radio radio) {

        if (Objects.isNull(radio)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        return radioMapper.insert(radio) > 0;
    }

    @Override
    public ListRadioDto listRadioByUserId(Integer pageNum, Integer pageSize, String userId, String searchTitle) {

        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .eq(Radio::getUserId, userId)
                .eq(Radio::getStatus, RadioStatus.NORMAL)
                .orderByDesc(Radio::getCreateTime);

        if (!StringUtils.isEmpty(searchTitle)) {
            query.like(Radio::getRadioIntro, searchTitle);
        }


        PageHelper.startPage(pageNum, pageSize);
        List<Radio> radioList = radioMapper.selectList(query);
        PageInfo<Radio> pageInfo = new PageInfo<>(radioList);

        ListRadioDto listRadioDto = new ListRadioDto(pageInfo);
        listRadioDto.setRadioList(pageInfo.getList());

        // 绑定电台用户
        bindRadioMember(listRadioDto.getRadioList());
        // 绑定数据
        contentService.bindRadioContentData(listRadioDto.getRadioList());

        return listRadioDto;
    }

    @Override
    public Radio getByRadioId(String radioId) {
        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .eq(Radio::getRadioId, radioId);
        Radio radio = radioMapper.selectOne(query);
        Asserts.notNull(radio, ErrorCode.RADIO_NOT_EXISTS);
        return radio;
    }

    @Override
    public List<Radio> listByRadioId(List<String> radioIdList) {
        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .in(Radio::getRadioId, radioIdList);
        return radioMapper.selectList(query);
    }

    @Override
    public Integer updateRadio(Radio radio) {
        if (Objects.isNull(radio)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if (Objects.isNull(radio.getRadioId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        return radioMapper.updateById(radio);
    }

    @Override
    public ListRadioDto list(Integer pageNum, Integer pageSize) {


        PageHelper.startPage(pageNum, pageSize);
        List<Radio> radioList = radioMapper.list();
        PageInfo<Radio> pageInfo = new PageInfo<>(radioList);

        ListRadioDto listRadioDto = new ListRadioDto(pageInfo);
        listRadioDto.setRadioList(pageInfo.getList());

        // 绑定电台用户
        bindRadioMember(listRadioDto.getRadioList());

        // 绑定数据
        contentService.bindRadioContentData(listRadioDto.getRadioList());

        return listRadioDto;
    }


    @Override
    public void bindRadioMember(List<Radio> radioList) {
        if (CollectionUtils.isEmpty(radioList)) {
            return;
        }

        // 绑定用户
        Set<String> userIdList = radioList.stream().map(Radio::getUserId).collect(Collectors.toSet());
        List<String> latestCommentUserId = radioList.stream().map(Radio::getLatestCommentUserId).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(latestCommentUserId)) {
            userIdList.addAll(latestCommentUserId);
        }
        List<String> userIdDistinct = userIdList.stream().distinct().collect(Collectors.toList());


        List<Member> memberList = memberService.listByUserIdList(Lists.newArrayList(userIdDistinct));
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        radioList.forEach(radio -> {
            memberList.forEach(member -> {
                if (radio.getUserId().equals(member.getUserId())) {
                    radio.setMember(member);
                }
                if (Objects.nonNull(radio.getLatestCommentTime()) && Objects.nonNull(radio.getLatestCommentUserId())) {
                    if (radio.getLatestCommentUserId().equals(member.getUserId())) {
                        radio.setLatestCommentUserName(member.getUserName());
                    }
                }
            });
        });
    }

    @Override
    public Integer total() {
        return radioMapper.total();
    }

    @Override
    public Boolean userHasRadio(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return Boolean.FALSE;
        }
        return radioMapper.countRadioByUserId(userId) > 0;
    }

}
