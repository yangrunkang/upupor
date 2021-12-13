package com.upupor.service.service.aggregation;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.dto.page.RadioIndexDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 * 电台聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/15 23:05
 */
@Service
@RequiredArgsConstructor
public class RadioAggregateService {

    private final RadioService radioService;

    private final MemberService memberService;

    private final FileService fileService;

    private final CommentService commentService;

    private final ContentService contentService;

    private final ViewerService viewerService;


    public RadioIndexDto index(Integer pageNum, Integer pageSize) {
        ListRadioDto listRadioDto = radioService.list(pageNum, pageSize);

        // 个人主页电台列表添加广告
        if (!CollectionUtils.isEmpty(listRadioDto.getRadioList())) {
            boolean exists = listRadioDto.getRadioList().parallelStream().anyMatch(t -> t.getRadioId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = listRadioDto.getRadioList().size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Radio radio = new Radio();
                radio.setRadioId(CcConstant.GoogleAd.FEED_AD);
                radio.setUserId(CcConstant.GoogleAd.FEED_AD);
                radio.setCreateTime(CcDateUtil.getCurrentTime());

//                Member member = new Member();
//                member.setUserId(CcConstant.GoogleAd.FEED_AD);
//                member.setVia(CcConstant.GoogleAd.FEED_AD);
//                member.setUserName(CcConstant.GoogleAd.FEED_AD);
//                radio.setMember(member);
                listRadioDto.getRadioList().add(adIndex, radio);
            }
        }

        RadioIndexDto radioIndexDto = new RadioIndexDto();
        radioIndexDto.setListRadioDto(listRadioDto);
        return radioIndexDto;
    }


    public MemberIndexDto manageRadio(Integer pageNum, Integer pageSize, String userId, String searchTitle) {

        MemberIndexDto memberIndexDto = new MemberIndexDto();
        ListRadioDto listRadioDto = radioService.listRadioByUserId(pageNum, pageSize, userId, searchTitle);
        memberIndexDto.setListRadioDto(listRadioDto);
        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }

    public RadioIndexDto detail(String radioId, Integer pageNum, Integer pageSize) {

        Radio radio = radioService.getByRadioId(radioId);
        if (Objects.isNull(radio)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_EXISTS);
        }

        File fileByFileUrl = fileService.selectByFileUrl(radio.getRadioUrl());
        if (Objects.nonNull(fileByFileUrl)) {
            radio.setUploadStatus(fileByFileUrl.getUploadStatus());
        }

        contentService.viewNumPlusOne(radioId);

        // 绑定评论
        commentService.bindRadioComment(radio, pageNum, pageSize);

        // 绑定作者
        memberService.bindRadioMember(radio);

        // 绑定数据
        contentService.bindRadioContentData(Collections.singletonList(radio));

        // 绑定访问者
        radio.setViewerList(viewerService.listViewerByTargetIdAndType(radioId, CcEnum.ViewTargetType.CONTENT));

        RadioIndexDto radioIndexDto = new RadioIndexDto();
        radioIndexDto.setRadio(radio);
        return radioIndexDto;
    }


}
