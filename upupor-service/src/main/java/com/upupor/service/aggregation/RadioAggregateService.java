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

package com.upupor.service.aggregation;

import com.upupor.data.dao.entity.File;
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dto.page.RadioIndexDto;
import com.upupor.data.dto.page.ad.AbstractAd;
import com.upupor.data.dto.page.common.ListRadioDto;
import com.upupor.data.types.UploadStatus;
import com.upupor.data.types.ViewTargetType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.base.*;
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
        } else {
            radio.setUploadStatus(UploadStatus.UPLOAD_FAILED);
        }

        contentService.viewNumPlusOne(radioId);

        // 绑定评论
        commentService.bindRadioComment(radio, pageNum, pageSize);

        // 绑定作者
        memberService.bindRadioMember(radio);

        // 绑定数据
        contentService.bindRadioContentData(Collections.singletonList(radio));

        // 绑定访问者
        radio.setViewerList(viewerService.listViewerByTargetIdAndType(radioId, ViewTargetType.CONTENT));

        RadioIndexDto radioIndexDto = new RadioIndexDto();
        radioIndexDto.setRadio(radio);
        return radioIndexDto;
    }


}
