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

package com.upupor.service.business.manage;

import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.service.CssPatternService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.utils.ServletUtils;

import javax.annotation.Resource;

/**
 * 管理抽象
 *
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
public abstract class AbstractManageInfoGet {

    @Resource
    private MemberService memberService;

    @Resource
    private CssPatternService cssPatternService;

    /**
     * 管理统一返回的IndexDto
     */
    private MemberIndexDto memberIndexDto = new MemberIndexDto();

    /**
     * 获取用户信息
     */
    public MemberIndexDto initMemberInfo() {
        memberIndexDto = new MemberIndexDto();
        String userId =ServletUtils.getUserId();
        Member member = memberService.memberInfoData(userId);
        member.setMemberConfig(member.getMemberConfig());
        memberIndexDto.setMember(member);
        memberIndexDto.setListCssPatternDto(cssPatternService.getAll(userId));
        return memberIndexDto;
    }

    protected MemberIndexDto getMemberIndexDto() {
        return memberIndexDto;
    }

    /**
     * 指定Dto处理
     *
     * @param manageDto
     */
    protected abstract void specifyDtoHandle(ManageDto manageDto);

    /**
     * 暴露出去
     *
     * @return
     */
    public MemberIndexDto getData(ManageDto manageDto) {
        initMemberInfo();
        specifyDtoHandle(manageDto);
        return memberIndexDto;
    }


}
