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

package com.upupor.web.api;

import com.upupor.framework.CcResponse;
import com.upupor.lucene.UpuporLucene;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.business.member.MemberOperateService;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.outer.req.member.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 用户
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:51
 */
@Slf4j
@Api(tags = "用户服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberOperateService memberBusinessService;

    @ApiOperation("用户登录")
    @PostMapping("login")
    @UpuporLimit(limitType = LimitType.LOGIN, needLogin = false)
    @Upgrade2ApiSuccess
    public CcResponse get(@RequestBody MemberLoginReq memberLoginReq) {
        return memberBusinessService.run(MemberBusiness.LOGIN, memberLoginReq);
    }

    @ApiOperation("用户注册")
    @PostMapping("register")
    @UpuporLucene(dataType = LuceneDataType.MEMBER, operationType = LuceneOperationType.ADD)
    public CcResponse add(AddMemberReq addMemberReq) throws Exception {
        return memberBusinessService.run(MemberBusiness.REGISTER, addMemberReq);
    }

    @ApiOperation("登出")
    @GetMapping("/logout")
    public CcResponse logoutConfirm() {
        return memberBusinessService.run(MemberBusiness.LOGIN_OUT);
    }

    @ApiOperation("编辑用户")
    @PostMapping("edit")
    @UpuporLucene(dataType = LuceneDataType.MEMBER, operationType = LuceneOperationType.UPDATE)
    public CcResponse edit(UpdateMemberReq updateMemberReq) throws Exception {
        return memberBusinessService.run(MemberBusiness.EDIT_MEMBER_INFO, updateMemberReq);
    }

    @ApiOperation("设置背景样式")
    @PostMapping("edit/bg-style-settings")
    public CcResponse bgStyleSettings(UpdateCssReq updateCssReq) throws Exception {
        return memberBusinessService.run(MemberBusiness.BG_SETTINGS, updateCssReq);
    }

    @ApiOperation("设置用户喜爱的文章类型")
    @PostMapping("edit/default-content-type-settings")
    public CcResponse defaultContentTypeSetting(UpdateDefaultContentTypeReq updateDefaultContentTypeReq) throws Exception {
        return memberBusinessService.run(MemberBusiness.DEFAULT_CONTENT_TYPE, updateDefaultContentTypeReq);
    }

    @ApiOperation("重置密码")
    @PostMapping("resetPassword")
    public CcResponse resetPassword(UpdatePasswordReq updatePasswordReq) {
        return memberBusinessService.run(MemberBusiness.RESET_PASSWORD, updatePasswordReq);
    }

    @ApiOperation("发送验证码")
    @PostMapping("/sendVerifyCode")
    @UpuporLimit(limitType = LimitType.SEND_EMAIL_VERIFY_CODE, needLogin = false, needSpendMoney = true)
    public CcResponse sendVerifyCode(SendVerifyCodeReq addVerifyCodeReq) {
        return memberBusinessService.run(MemberBusiness.SEND_VERIFY_CODE, addVerifyCodeReq);
    }

    @ApiOperation("领取今日积分")
    @PostMapping("dailyPoints")
    public CcResponse dailyPoints() {
        return memberBusinessService.run(MemberBusiness.DAILY_POINTS);
    }

    @ApiOperation("变更头像")
    @PostMapping("updateVia")
    public CcResponse updateVia(UpdateViaReq updateViaReq) {
        return memberBusinessService.run(MemberBusiness.VIA, updateViaReq);
    }

}
