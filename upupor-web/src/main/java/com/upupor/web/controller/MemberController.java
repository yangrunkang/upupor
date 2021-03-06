/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.web.controller;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.lucene.UpuporLucene;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.MemberConfig;
import com.upupor.service.data.dao.mapper.MemberConfigMapper;
import com.upupor.service.data.service.FileService;
import com.upupor.service.data.service.MemberIntegralService;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.data.service.MessageService;
import com.upupor.service.dto.OperateMemberDto;
import com.upupor.service.listener.event.MemberRegisterEvent;
import com.upupor.service.outer.req.*;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

import static com.upupor.framework.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;


/**
 * ??????
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:51
 */
@Slf4j
@Api(tags = "????????????")
@RestController
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;
    private final MessageService messageService;
    private final MemberIntegralService memberIntegralService;
    private final FileService fileService;
    private final String register = "register";
    private final String forgetPassword = "forgetPassword";
    private final ApplicationEventPublisher eventPublisher;
    private final UpuporConfig upuporConfig;
    private final MemberConfigMapper memberConfigMapper;

    @ApiOperation("????????????")
    @PostMapping("login")
    @ResponseBody
    @UpuporLimit(limitType = LimitType.LOGIN,needLogin = false)
    public CcResponse get(GetMemberReq getMemberReq) {
        CcResponse cc = new CcResponse();
        Boolean login = memberService.login(getMemberReq);
        if (!login) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        cc.setData(true);
        return cc;
    }


    @ApiOperation("????????????")
    @PostMapping("register")
    @ResponseBody
    @UpuporLucene(dataType = LuceneDataType.MEMBER, operationType = LuceneOperationType.ADD)
    public CcResponse add(AddMemberReq addMemberReq) {

        CcResponse cc = new CcResponse();

        if (StringUtils.isEmpty(addMemberReq.getUserName())) {
            throw new BusinessException(ErrorCode.USER_NAME_CAN_NOT_EMPTY);
        } else {
            checkUserName(addMemberReq.getUserName());
        }

        // ????????????????????????
        memberService.checkUserExists(addMemberReq);

        // ?????????????????????????????? ???????????? RedisKey??????: source + email + ?????????
        String key = register + addMemberReq.getEmail().trim() + addMemberReq.getVerifyCode();
        String value = RedisUtil.get(key);
        if (StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // ???????????????????????????
        if (!addMemberReq.getVerifyCode().trim().equals(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        Member member = memberService.register(addMemberReq);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.REGISTER_FAILED);
        }

        // ???????????????????????????
        // ??????????????????Session
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_ID, member.getUserId());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, member.getVia());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_NAME, member.getUserName());

        // ????????????
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


    @ApiOperation("??????")
    @GetMapping("/logout")
    @ResponseBody
    public CcResponse logoutConfirm() {
        // ??????session
        ServletUtils.getSession().invalidate();

        return new CcResponse();
    }

    @ApiOperation("????????????")
    @PostMapping("edit")
    @ResponseBody
    @UpuporLucene(dataType = LuceneDataType.MEMBER, operationType = LuceneOperationType.UPDATE)
    public CcResponse edit(UpdateMemberReq updateMemberReq) throws Exception {
        CcResponse cc = new CcResponse();

        if (StringUtils.isEmpty(updateMemberReq.getUserName())) {
            throw new BusinessException(ErrorCode.USER_NAME_CAN_NOT_EMPTY);
        } else {
            checkUserName(updateMemberReq.getUserName());
        }

        String userId = ServletUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }

        updateMemberReq.setUserId(userId);
        Boolean editSuccess = memberService.editMember(updateMemberReq);
        if (!editSuccess) {
            throw new BusinessException(ErrorCode.EDIT_MEMBER_FAILED);
        }
        // ???????????????
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_NAME, updateMemberReq.getUserName());

        Member reGet = memberService.memberInfo(userId);

        OperateMemberDto operateMemberDto = new OperateMemberDto();
        operateMemberDto.setMemberId(reGet.getUserId());
        operateMemberDto.setSuccess(Boolean.TRUE);
        operateMemberDto.setStatus(reGet.getStatus());
        cc.setData(operateMemberDto);
        return cc;
    }

    private void checkUserName(String userName) {
        if (userName.toLowerCase().contains("test") || userName.contains("??????")) {
            throw new BusinessException(ErrorCode.USER_NAME_ERROR);
        }
    }

    @ApiOperation("??????????????????")
    @PostMapping("edit/bg-style-settings")
    @ResponseBody
    public CcResponse bgStyleSettings(UpdateCssReq updateCssReq) throws Exception {
        CcResponse cc = new CcResponse();
        String userId = ServletUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }

        updateCssReq.setUserId(userId);
        Boolean editSuccess = memberService.editMemberBgStyle(updateCssReq);
        if (!editSuccess) {
            throw new BusinessException(ErrorCode.SETTING_BG_FAILED);
        }
        cc.setData(true);
        return cc;
    }

    @ApiOperation("?????????????????????????????????")
    @PostMapping("edit/default-content-type-settings")
    @ResponseBody
    public CcResponse defaultContentTypeSetting(UpdateDefaultContentTypeReq updateCssReq) throws Exception {
        CcResponse cc = new CcResponse();
        String userId = ServletUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }

        Member member = memberService.memberInfo(userId);
        MemberConfig memberConfig = member.getMemberConfig();
        if (Objects.isNull(memberConfig)) {
            throw new BusinessException(ErrorCode.MEMBER_CONFIG_LESS);
        }
        memberConfig.setDefaultContentType(updateCssReq.getSelectedContentType());
        int result = memberConfigMapper.updateById(memberConfig);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SETTING_DEFAULT_CONTENT_TYPE_FAILED);
        }
        cc.setData(true);
        return cc;
    }

    @ApiOperation("????????????")
    @PostMapping("resetPassword")
    @ResponseBody
    public CcResponse resetPassword(UpdatePasswordReq updatePasswordReq) {
        CcResponse cc = new CcResponse();
        String email = updatePasswordReq.getEmail().trim();
        if (StringUtils.isEmpty(email)) {
            throw new BusinessException(ErrorCode.EMAIL_EMPTY);
        }

        // ?????????????????????
        memberService.checkUserExists(email);

        // ?????????????????????????????? ???????????? RedisKey??????: source + email + ?????????
        String key = forgetPassword + updatePasswordReq.getEmail().trim() + updatePasswordReq.getVerifyCode();
        String value = RedisUtil.get(key);
        if (StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // ???????????????????????????
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

    @ApiOperation("???????????????")
    @PostMapping("/sendVerifyCode")
    @ResponseBody
    @UpuporLimit(limitType = LimitType.SEND_EMAIL_VERIFY_CODE, needLogin = false, needSpendMoney = true)
    public CcResponse sendVerifyCode(AddVerifyCodeReq addVerifyCodeReq) {
        CcResponse ccResponse = new CcResponse();

        String verifyCode = CcUtils.getVerifyCode();

        String email = addVerifyCodeReq.getEmail().trim();
        if (!email.contains(CcConstant.AT)) {
            throw new BusinessException(ErrorCode.WRONG_EMAIL);
        }

        String emailTitle;
        String emailContent = "?????????: " + verifyCode;
        if (addVerifyCodeReq.getSource().equals(register)) {
            emailTitle = "Upupor???????????????";

        } else if (addVerifyCodeReq.getSource().equals(forgetPassword)) {
            // ???????????????????????????
            memberService.checkUserExists(addVerifyCodeReq.getEmail());
            emailTitle = "Upupor????????????";
        } else {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "?????????????????????????????????");
        }

        if ("dev".equals(upuporConfig.getEnv())) {
            log.info("?????????????????????:{}", verifyCode);
        }

        // ???????????????????????????????????????????????????
        messageService.sendEmail(email, emailTitle, emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);

        // Redis??????90s ???????????? RedisKey??????: source + email + ?????????
        String key = addVerifyCodeReq.getSource() + addVerifyCodeReq.getEmail().trim() + verifyCode;
        RedisUtil.set(key, verifyCode, 90L);
        ccResponse.setData(true);
        return ccResponse;
    }

    @ApiOperation("??????????????????")
    @PostMapping("dailyPoints")
    @ResponseBody
    public CcResponse dailyPoints() {

        CcResponse ccResponse = new CcResponse();

        Boolean exists = memberService.checkIsGetDailyPoints();
        if (exists) {
            throw new BusinessException(ErrorCode.ALREADY_GET_DAILY_POINTS);
        }

        String userId = ServletUtils.getUserId();

        memberIntegralService.addIntegral(IntegralEnum.DAILY_POINTS, userId, userId);
        return ccResponse;
    }

    @ApiOperation("????????????")
    @PostMapping("updateVia")
    @ResponseBody
    public CcResponse updateVia(UpdateViaReq updateViaReq) {

        CcResponse ccResponse = new CcResponse();

        String via = updateViaReq.getVia();
        File fileByFileUrl = fileService.selectByFileUrl(via);
        if (Objects.isNull(fileByFileUrl)) {
            throw new BusinessException(ErrorCode.SELECTED_VIA_NOT_EXISTS);
        }
        String userId = ServletUtils.getUserId();
        Member member = memberService.memberInfo(userId);
        member.setVia(via);
        member.setSysUpdateTime(new Date());
        Boolean result = memberService.update(member);
        if (result) {
            ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, via);
        }

        ccResponse.setData(result);
        return ccResponse;
    }


}
