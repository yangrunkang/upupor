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
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.dto.OperateMemberDto;
import com.upupor.service.listener.event.MemberRegisterEvent;
import com.upupor.service.outer.req.member.AddMemberReq;
import com.upupor.service.utils.ServletUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 14:15
 * @email: yangrunkang53@gmail.com
 */
@Component
public class Register extends AbstractMember<AddMemberReq> {
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.REGISTER;
    }

    @Override
    public CcResponse handle() {
        CcResponse cc = new CcResponse();

        AddMemberReq addMemberReq = transferReq();

        checkUserName(addMemberReq.getUserName());

        // 检测用户是否存在
        memberService.checkUserExists(addMemberReq);

        // 先校验验证码是否存在 用户注册 RedisKey组成: source + email + 验证码
        String key = REGISTER + addMemberReq.getEmail().trim() + addMemberReq.getVerifyCode();
        String value = RedisUtil.get(key);
        if (StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // 检查验证码是否正确
        if (!addMemberReq.getVerifyCode().trim().equals(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        Member member = memberService.register(addMemberReq);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.REGISTER_FAILED);
        }

        // 注册成功后自动登录
        // 设置登录成功Session
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_ID, member.getUserId());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, member.getVia());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_NAME, member.getUserName());

        // 注册消息
        MemberRegisterEvent memberRegisterEvent = new MemberRegisterEvent();
        memberRegisterEvent.setUserId(member.getUserId());
        eventPublisher.publishEvent(memberRegisterEvent);

        OperateMemberDto operateMemberDto = new OperateMemberDto();
        operateMemberDto.setMemberId(member.getUserId());
        operateMemberDto.setSuccess(Boolean.TRUE);
        operateMemberDto.setStatus(member.getStatus());

        cc.setData(operateMemberDto);
        return cc;
    }
}
