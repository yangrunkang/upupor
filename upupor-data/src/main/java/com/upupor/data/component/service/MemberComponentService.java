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
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.mapper.MemberMapper;
import com.upupor.data.types.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-12-03 16:15
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class MemberComponentService implements MemberComponent {
    private final MemberMapper memberMapper;

    @Override
    public Boolean loginModel(LoginModel loginModel) {
        LambdaQueryWrapper<Member> loginQuery = new LambdaQueryWrapper<Member>()
                .eq(Member::getEmail, loginModel.getEmail())
                .eq(Member::getPassword, loginModel.getSecretPassword())
                .eq(Member::getStatus, MemberStatus.NORMAL);
        Member member = memberMapper.selectOne(loginQuery);
        return Objects.nonNull(member);
    }
}
