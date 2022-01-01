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

package com.upupor.service.business.aggregation;

import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.types.MemberIsAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.upupor.service.utils.ServletUtils.getUserId;

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
        Member member = memberService.memberInfoData(userId);
        if (!member.getIsAdmin().equals(MemberIsAdmin.ADMIN)) {
            throw new BusinessException(ErrorCode.USER_NOT_ADMIN);
        }
        memberIndexDto.setMember(member);
        return memberIndexDto;
    }


    public ContentIndexDto adminContent(Integer pageNum, Integer pageSize, String contentId) {
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        ContentIndexDto contentIndexDto = new ContentIndexDto();
        contentIndexDto.setContent(content);
        return contentIndexDto;
    }

}
