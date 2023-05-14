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

import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dto.page.ContentIndexDto;
import com.upupor.data.dto.page.MemberIndexDto;
import com.upupor.data.types.MemberIsAdmin;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.upupor.service.utils.SessionUtils.getUserId;

/**
 * 用户聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 23:54
 */
@Service
@RequiredArgsConstructor
public class AdminAggregateService {

    private final MemberService memberService;
    private final ContentService contentService;


    public MemberIndexDto admin(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();
        MemberEnhance memberEnhance = memberService.memberInfoData(userId);
        Member member = memberEnhance.getMember();
        if (!member.getIsAdmin().equals(MemberIsAdmin.ADMIN)) {
            throw new BusinessException(ErrorCode.USER_NOT_ADMIN);
        }
        memberIndexDto.setMemberEnhance(memberEnhance);
        return memberIndexDto;
    }


    public ContentIndexDto adminContent(Integer pageNum, Integer pageSize, String contentId) {
        ContentEnhance contentEnhance = contentService.getContentByContentIdNoStatus(contentId);
        ContentIndexDto contentIndexDto = new ContentIndexDto();
        contentIndexDto.setContentEnhance(contentEnhance);
        return contentIndexDto;
    }

}
