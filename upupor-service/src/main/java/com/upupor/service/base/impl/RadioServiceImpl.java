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
import com.google.common.collect.Lists;
import com.upupor.data.dao.entity.File;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ContentDataEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dao.entity.enhance.RadioEnhance;
import com.upupor.data.dao.mapper.CommentMapper;
import com.upupor.data.dao.mapper.RadioMapper;
import com.upupor.data.dto.OperateRadioDto;
import com.upupor.data.dto.dao.CommentNumDto;
import com.upupor.data.dto.page.common.ListRadioDto;
import com.upupor.data.types.RadioStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.FileService;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.RadioService;
import com.upupor.service.outer.req.AddRadioReq;
import com.upupor.service.outer.req.DelRadioReq;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
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
    private final CommentMapper commentMapper;
    private final MemberService memberService;
    private final ContentService contentService;
    private final FileService fileService;

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
        List<RadioEnhance> radioEnhanceList = Converter.radioEnhanceList(radioList);
        PageInfo<Radio> pageInfo = new PageInfo<>(radioList);

        ListRadioDto listRadioDto = new ListRadioDto(pageInfo);
        listRadioDto.setRadioEnhanceList(radioEnhanceList);

        // 绑定电台用户
        bindRadioMember(radioEnhanceList);
        // 绑定数据
        bindRadioContentData(radioEnhanceList);

        return listRadioDto;
    }

    @Override
    public RadioEnhance getByRadioId(String radioId) {
        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .eq(Radio::getRadioId, radioId)
                .in(Radio::getStatus, RadioStatus.notDeleteStatus());
        Radio radio = radioMapper.selectOne(query);
        Asserts.notNull(radio, ErrorCode.RADIO_NOT_EXISTS);
        return Converter.radioEnhance(radio);
    }

    @Override
    public List<Radio> listByRadioIdList(List<String> radioIdList) {
        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .in(Radio::getRadioId, radioIdList)
                .eq(Radio::getStatus, RadioStatus.NORMAL);
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
        List<RadioEnhance> radioEnhanceList = Converter.radioEnhanceList(radioList);

        ListRadioDto listRadioDto = new ListRadioDto(pageInfo);
        listRadioDto.setRadioEnhanceList(radioEnhanceList);

        // 绑定电台用户
        bindRadioMember(radioEnhanceList);

        // 绑定数据
        bindRadioContentData(radioEnhanceList);

        return listRadioDto;
    }


    @Override
    public void bindRadioMember(List<RadioEnhance> radioList) {
        if (CollectionUtils.isEmpty(radioList)) {
            return;
        }

        // 绑定用户
        Set<String> userIdList = radioList.stream()
                .map(RadioEnhance::getRadio)
                .map(Radio::getUserId)
                .collect(Collectors.toSet());
        List<String> latestCommentUserId = radioList.stream()
                .map(RadioEnhance::getRadio)
                .map(Radio::getLatestCommentUserId).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(latestCommentUserId)) {
            userIdList.addAll(latestCommentUserId);
        }
        List<String> userIdDistinct = userIdList.stream().distinct().collect(Collectors.toList());


        List<MemberEnhance> memberEnhanceList = memberService.listByUserIdList(Lists.newArrayList(userIdDistinct));
        if (CollectionUtils.isEmpty(memberEnhanceList)) {
            return;
        }

        radioList.forEach(radioEnhance -> {
            memberEnhanceList.forEach(memberEnhance -> {
                Radio radio = radioEnhance.getRadio();
                Member member = memberEnhance.getMember();
                if (radio.getUserId().equals(member.getUserId())) {
                    radioEnhance.setMemberEnhance(memberEnhance);
                }
                if (Objects.nonNull(radio.getLatestCommentTime()) && Objects.nonNull(radio.getLatestCommentUserId())) {
                    if (radio.getLatestCommentUserId().equals(member.getUserId())) {
                        radioEnhance.setLatestCommentUserName(member.getUserName());
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

    @Override
    public OperateRadioDto createNewRadio(AddRadioReq addRadioReq) {
        if (Objects.isNull(addRadioReq.getFileUrl())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "音频文件为空");
        }

        if (Objects.isNull(addRadioReq.getRadioIntro())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "音频简介为空");
        }

        // 获取用户
        Member member = memberService.memberInfo(SessionUtils.getUserId()).getMember();
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }

        // 检查文件是否上传成功
        File file = fileService.selectByFileUrl(addRadioReq.getFileUrl());
        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_EXITS_IN_DB);
        }

        Radio radio = new Radio();
        radio.setRadioId(CcUtils.getUuId());
        radio.setUserId(member.getUserId());
        radio.setRadioIntro(addRadioReq.getRadioIntro());
        radio.setRadioUrl(file.getFileUrl());
        radio.setContentId(null);
        radio.setStatus(RadioStatus.NORMAL);
        radio.setCreateTime(CcDateUtil.getCurrentTime());
        radio.setLatestCommentTime(CcDateUtil.getCurrentTime());
        radio.setSysUpdateTime(new Date());

        if (!this.addRadio(radio)) {
            throw new BusinessException(ErrorCode.UPLOAD_RADIO_ERROR);
        }

        // 初始化数据
        contentService.initContendData(radio.getRadioId());


        OperateRadioDto operateRadioDto = new OperateRadioDto();
        operateRadioDto.setRadioId(radio.getRadioId());
        operateRadioDto.setSuccess(Boolean.TRUE);
        operateRadioDto.setStatus(radio.getStatus());

        return operateRadioDto;
    }


    @Override
    public OperateRadioDto deleteRadio(DelRadioReq delRadioReq) {
        if (Objects.isNull(delRadioReq) || StringUtils.isEmpty(delRadioReq.getRadioId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String userId = SessionUtils.getUserId();

        RadioEnhance radioEnhance = this.getByRadioId(delRadioReq.getRadioId());
        Radio radio = radioEnhance.getRadio();
        if (Objects.isNull(radio)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_EXISTS);
        }

        if (!radio.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_BELONG_TO_YOU);
        }

        radio.setStatus(RadioStatus.DELETED);
        radio.setSysUpdateTime(new Date());
        Boolean success = this.updateRadio(radio) > 0;


        OperateRadioDto operateRadioDto = new OperateRadioDto();
        operateRadioDto.setRadioId(radio.getRadioId());
        operateRadioDto.setSuccess(success);
        operateRadioDto.setStatus(radio.getStatus());
        return operateRadioDto;
    }

    @Override
    public void bindRadioContentData(List<RadioEnhance> radioEnhanceList) {

        if (CollectionUtils.isEmpty(radioEnhanceList)) {
            return;
        }

        List<String> radioIdList = radioEnhanceList.stream()
                .map(RadioEnhance::getRadio)
                .map(Radio::getRadioId).
                distinct().collect(Collectors.toList());
        List<ContentDataEnhance> contentDataEnhanceList = contentService.getContentData(radioIdList);
        if (CollectionUtils.isEmpty(contentDataEnhanceList)) {
            return;
        }
        // 浏览数不用处理
        // 点赞数也不用处理
        // 需要处理评论数
        List<CommentNumDto> commentNumDtoList = commentMapper.selectByCommentIdList(radioIdList);
        if (!CollectionUtils.isEmpty(commentNumDtoList)) {
            contentDataEnhanceList.forEach(radioData -> commentNumDtoList.forEach(commentNumDto -> {
                if (radioData.getContentData().getContentId().equals(commentNumDto.getContentId())) {
                    radioData.getContentData().setCommentNum(commentNumDto.getTotal());
                }
            }));
        }

        // 绑定文章数据
        contentDataEnhanceList.forEach(contentDataEnhance -> {
            radioEnhanceList.forEach(radio -> {
                if (contentDataEnhance.getContentData().getContentId().equals(radio.getRadio().getRadioId())) {
                    radio.setContentDataEnhance(contentDataEnhance);
                }
            });
        });

    }

}
