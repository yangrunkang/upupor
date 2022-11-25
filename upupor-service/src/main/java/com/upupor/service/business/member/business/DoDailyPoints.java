/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.service.business.member.business;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.service.base.MemberIntegralService;
import com.upupor.service.outer.req.member.BaseMemberReq;
import com.upupor.framework.utils.ServletUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:22
 * @email: yangrunkang53@gmail.com
 */
@Component
public class DoDailyPoints extends AbstractMember<BaseMemberReq> {

    @Resource
    private MemberIntegralService memberIntegralService;

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.DAILY_POINTS;
    }

    @Override
    public CcResponse handle() {
        CcResponse ccResponse = new CcResponse();

        Boolean exists = memberService.checkIsGetDailyPoints();
        if (exists) {
            throw new BusinessException(ErrorCode.ALREADY_GET_DAILY_POINTS);
        }

        String userId = ServletUtils.getUserId();

        memberIntegralService.addIntegral(IntegralEnum.DAILY_POINTS, userId, userId);
        return ccResponse;
    }
}
