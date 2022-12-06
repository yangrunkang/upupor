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
import com.upupor.api.response.user.DetailResp;
import com.upupor.api.response.user.LoginResp;
import com.upupor.api.response.user.RegisterResp;
import com.upupor.data.component.MemberComponent;
import com.upupor.data.component.model.EmailLoginModel;
import com.upupor.data.component.model.RegisterModel;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.utils.PasswordUtils;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.security.jwt.JwtMemberModel;
import com.upupor.security.jwt.UpuporMemberJwt;
import com.upupor.service.base.MemberService;
import com.upupor.service.utils.AvatarHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-26 23:20
 * @email: yangrunkang53@gmail.com
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-app")
public class UserApp implements UserApi {
    private final MemberComponent memberComponent;
    private final MemberService memberService;

    @PostMapping("/register")
    @Override
    public ApiResp<RegisterResp> register(@RequestBody RegisterReq registerReq) {
        String userId = CcUtils.getUuId();
        long createTime = CcDateUtil.getCurrentTime();
        Member member = memberComponent.registerModel(RegisterModel.builder()
                .userId(userId)
                .email(registerReq.getEmail())
                .userName(registerReq.getUserName())
                .via(AvatarHelper.generateAvatar(Math.abs(userId.hashCode())))
                .secretPassword(PasswordUtils.encryptMemberPassword(registerReq.getEmail(), userId, createTime))
                .createTime(createTime)
                .build());
        if (Objects.isNull(member)) {
            throw new ApiException(ApiErrorCode.REGISTER_FAILED);
        }
        return new ApiResp<>(new RegisterResp());
    }

    @PostMapping("/login")
    @Override
    public ApiResp<LoginResp> login(@RequestBody LoginReq loginReq) {
        Member member = memberComponent.emailLoginModel(EmailLoginModel.builder()
                .email(loginReq.getEmail())
                .build());
        if (Objects.isNull(member)) {
            throw new ApiException(ApiErrorCode.LOGIN_FAILED);
        }
        LoginResp loginResp = new LoginResp();
        loginResp.setToken(UpuporMemberJwt.createToken(JwtMemberModel.builder()
                .userId(member.getUserId())
                .expireTime(CcDateUtil.getCurrentTime() + 7 * 24 * 3600)
                .build()));
        return new ApiResp<>(loginResp);
    }

    @GetMapping("/detail")
    @Override
    public ApiResp<DetailResp> info(String userId) {
        MemberEnhance memberEnhance = memberService.memberInfo(userId);
        Member member = memberEnhance.getMember();

        DetailResp detailResp = new DetailResp();
        detailResp.setUserName(member.getUserName());
        detailResp.setUserVia(member.getVia());
        return new ApiResp<>(detailResp);
    }
}
