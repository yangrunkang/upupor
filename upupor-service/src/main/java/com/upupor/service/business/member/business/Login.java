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

import com.upupor.data.dao.entity.Member;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcRedis;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.security.jwt.JwtMemberModel;
import com.upupor.security.jwt.UpuporMemberJwt;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.business.data.LoginSuccessData;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.outer.req.member.MemberLoginReq;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:22
 * @email: yangrunkang53@gmail.com
 */
@Component
public class Login extends AbstractMember<MemberLoginReq> {

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.LOGIN;
    }

    @Override
    public CcResponse handle() {
        CcResponse cc = new CcResponse();
        MemberLoginReq memberLoginReq = transferReq();
        Member member = memberService.login(memberLoginReq);
        boolean loginFailed = Objects.isNull(member);
        if (loginFailed) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        String userId = member.getUserId();
        long tokenExpireTime = CcRedis.Operate.updateTokenExpireTime(userId);
        LoginSuccessData build = LoginSuccessData.builder()
                .token(UpuporMemberJwt.createToken(JwtMemberModel.builder()
                        .userId(userId)
                        .expireTime(tokenExpireTime)
                        .build()))
                .success(true)
                .build();
        cc.setData(build);
        return cc;
    }


}
