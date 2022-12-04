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

package com.upupor.data.component.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.data.component.MemberComponent;
import com.upupor.data.component.model.LoginModel;
import com.upupor.data.component.model.RegisterModel;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.MemberConfig;
import com.upupor.data.dao.entity.MemberExtend;
import com.upupor.data.dao.mapper.MemberConfigMapper;
import com.upupor.data.dao.mapper.MemberExtendMapper;
import com.upupor.data.dao.mapper.MemberMapper;
import com.upupor.data.types.MemberStatus;
import com.upupor.data.types.OpenEmail;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-12-03 16:15
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class MemberComponentService implements MemberComponent {
    private final MemberMapper memberMapper;
    private final MemberExtendMapper memberExtendMapper;
    private final MemberConfigMapper memberConfigMapper;

    @Override
    public Member loginModel(LoginModel loginModel) {
        LambdaQueryWrapper<Member> loginQuery = new LambdaQueryWrapper<Member>()
                .eq(Member::getEmail, loginModel.getEmail())
                .eq(Member::getPassword, loginModel.getSecretPassword())
                .eq(Member::getStatus, MemberStatus.NORMAL);
        return memberMapper.selectOne(loginQuery);
    }

    @Override
    public Member registerModel(RegisterModel registerModel) {
        /**
         * 用户信息
         */
        Member member = Member.builder()
                .userId(registerModel.getUserId())
                .userName(registerModel.getUserName())
                .email(registerModel.getEmail())
                .via(registerModel.getVia())
                .password(registerModel.getSecretPassword())
                .createTime(registerModel.getCreateTime())
                .status(MemberStatus.NORMAL)
                .build();
        member.setSysUpdateTime(new Date());

        /**
         * 用户拓展信息
         */
        MemberExtend memberExtend = MemberExtend.builder()
                .userId(registerModel.getUserId())
                .build();
        memberExtend.setSysUpdateTime(new Date());


        /**
         * 用户配置
         */
        MemberConfig memberConfig = MemberConfig.builder()
                .userId(registerModel.getUserId())
                .openEmail(OpenEmail.SUBSCRIBE_EMAIL)
                .createTime(CcDateUtil.getCurrentTime())
                .configId(CcUtils.getUuId())
                .build();
        memberConfig.setSysUpdateTime(new Date());

        // 入库
        memberMapper.insert(member);
        memberExtendMapper.insert(memberExtend);
        memberConfigMapper.insert(memberConfig);

        return member;
    }
}
