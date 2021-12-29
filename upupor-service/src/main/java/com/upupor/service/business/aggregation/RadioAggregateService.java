package com.upupor.service.business.aggregation;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.RadioIndexDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

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
        AbstractAd.ad(listRadioDto.getRadioList());
        RadioIndexDto radioIndexDto = new RadioIndexDto();
        radioIndexDto.setListRadioDto(listRadioDto);
        return radioIndexDto;
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
