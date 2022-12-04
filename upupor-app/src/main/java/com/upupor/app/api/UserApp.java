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

package com.upupor.app.api;

import com.upupor.api.UserApi;
import com.upupor.api.common.ApiErrorCode;
import com.upupor.api.common.ApiException;
import com.upupor.api.common.ApiResp;
import com.upupor.api.request.user.LoginReq;
import com.upupor.api.request.user.RegisterReq;
import com.upupor.api.response.user.LoginResp;
import com.upupor.api.response.user.RegisterResp;
import com.upupor.data.component.MemberComponent;
import com.upupor.data.component.model.LoginModel;
import com.upupor.data.dao.entity.Member;
import com.upupor.security.jwt.JwtMemberModel;
import com.upupor.security.jwt.UpuporMemberJwt;
import com.upupor.service.base.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-26 23:20
 * @email: yangrunkang53@gmail.com
 */
@Service
@RequiredArgsConstructor
public class UserApp implements UserApi {
    private final MemberComponent memberComponent;
    private final MemberService memberService;


    @Override
    public ApiResp<RegisterResp> register(RegisterReq registerReq) {


        return null;
    }

    @Override
    public ApiResp<LoginResp> login(LoginReq loginReq) {
        Member member = memberComponent.loginModel(LoginModel.builder()
                .email(loginReq.getEmail())
                .secretPassword(loginReq.getPassword())
                .build());
        if (Objects.nonNull(member)) {
            LoginResp loginResp = new LoginResp();
            loginResp.setToken(UpuporMemberJwt.createToken(JwtMemberModel.builder()
                    .userId(member.getUserId())
                    .build()));
            return new ApiResp<>(loginResp);
        }
        throw new ApiException(ApiErrorCode.LOGIN_FAILED);
    }
}