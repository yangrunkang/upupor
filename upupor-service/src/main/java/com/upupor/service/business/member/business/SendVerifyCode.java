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
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.data.service.MessageService;
import com.upupor.service.outer.req.member.SendVerifyCodeReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.framework.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:15
 * @email: yangrunkang53@gmail.com
 */
@Component
@Slf4j
public class SendVerifyCode extends AbstractMember<SendVerifyCodeReq> {

    @Resource
    private UpuporConfig upuporConfig;

    @Resource
    private MessageService messageService;

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.SEND_VERIFY_CODE;
    }

    @Override
    public CcResponse handle() {
        CcResponse ccResponse = new CcResponse();

        SendVerifyCodeReq addVerifyCodeReq = transferReq();

        String verifyCode = CcUtils.getVerifyCode();

        String email = addVerifyCodeReq.getEmail().trim();
        if (!email.contains(CcConstant.AT)) {
            throw new BusinessException(ErrorCode.WRONG_EMAIL);
        }

        String emailTitle;
        String emailContent = "验证码: " + verifyCode;
        if (addVerifyCodeReq.getSource().equals(REGISTER)) {
            emailTitle = "Upupor新用户注册";

        } else if (addVerifyCodeReq.getSource().equals(FORGET_PASSWORD)) {
            // 检测是否是本站用户
            memberService.checkUserExists(addVerifyCodeReq.getEmail());
            emailTitle = "Upupor重置密码";
        } else {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "发送验证码来源信息错误");
        }

        if ("dev".equals(upuporConfig.getEnv())) {
            log.info("开发环境验证码:{}", verifyCode);
        }

        // 验证码的短信要跳过是否订阅邮件配置
        messageService.sendEmail(email, emailTitle, emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);

        // Redis缓存90s 用户注册 RedisKey组成: source + email + 验证码
        String key = addVerifyCodeReq.getSource() + addVerifyCodeReq.getEmail().trim() + verifyCode;
        RedisUtil.set(key, verifyCode, 90L);
        ccResponse.setData(true);
        return ccResponse;
    }
}
