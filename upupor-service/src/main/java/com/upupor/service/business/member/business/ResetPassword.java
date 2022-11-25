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
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.framework.common.UserCheckFieldType;
import com.upupor.service.outer.req.member.UpdatePasswordReq;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.upupor.framework.CcRedisKey.memberVerifyCodeKey;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:06
 * @email: yangrunkang53@gmail.com
 */
@Component
public class ResetPassword extends AbstractMember<UpdatePasswordReq> {

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.RESET_PASSWORD;
    }

    @Override
    public CcResponse handle() {
        CcResponse cc = new CcResponse();
        UpdatePasswordReq updatePasswordReq = transferReq();

        String email = updatePasswordReq.getEmail().trim();
        if (StringUtils.isEmpty(email)) {
            throw new BusinessException(ErrorCode.EMAIL_EMPTY);
        }

        // 邮箱不存在,不能重设密码
        if (!memberService.checkUserExists(email, UserCheckFieldType.EMAIL)) {
            throw new BusinessException(ErrorCode.YOU_EMAIL_HAS_NOT_REGISTERED);
        }

        // 先校验验证码是否存在 用户注册 RedisKey组成: source + email + 验证码
        String key = memberVerifyCodeKey(FORGET_PASSWORD, updatePasswordReq.getEmail(), updatePasswordReq.getVerifyCode());
        String value = RedisUtil.get(key);
        if (StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // 检查验证码是否正确
        if (!updatePasswordReq.getVerifyCode().trim().equals(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        Boolean reset = memberService.resetPassword(updatePasswordReq);
        if (!reset) {
            throw new BusinessException(ErrorCode.RESET_PASSWORD_FAILURE);
        }
        cc.setData(true);
        return cc;
    }
}
