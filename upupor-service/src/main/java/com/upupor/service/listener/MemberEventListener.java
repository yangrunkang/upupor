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

package com.upupor.service.listener;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.data.dao.entity.Attention;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.service.MemberIntegralService;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.data.service.MessageService;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.listener.event.AttentionUserEvent;
import com.upupor.service.listener.event.MemberLoginEvent;
import com.upupor.service.listener.event.MemberRegisterEvent;
import com.upupor.service.outer.req.AddAttentionReq;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.types.MessageType;
import com.upupor.framework.utils.CcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.upupor.framework.CcConstant.MsgTemplate.PROFILE_EMAIL;
import static com.upupor.framework.CcConstant.MsgTemplate.PROFILE_INNER_MSG;


/**
 * ?????????????????????
 *
 * @author runkangyang (cruise)
 * @date 2020.03.07 23:02
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MemberEventListener {

    private static final String FORMAT_DAY = "yyyy-MM-dd";

    private final MemberIntegralService memberIntegralService;
    private final MemberService memberService;
    private final MessageService messageService;

    /**
     * ??????????????????
     *
     * @param memberLoginEvent
     */
    @EventListener
    @Async
    public void login(MemberLoginEvent memberLoginEvent) throws ParseException {
        // ???????????????
        loginIntegral(memberLoginEvent);
        // ????????????????????????
        recordLastLoginTime(memberLoginEvent);
    }

    /**
     * ????????????????????????
     *
     * @param memberLoginEvent
     */
    private void recordLastLoginTime(MemberLoginEvent memberLoginEvent) {
        String userId = memberLoginEvent.getUserId();
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        Member member = memberService.memberInfo(userId);
        if (Objects.isNull(member)) {
            return;
        }
        member.setLastLoginTime(CcDateUtil.getCurrentTime());
        memberService.update(member);
    }

    /**
     * ???????????????
     *
     * @param memberLoginEvent
     * @throws ParseException
     */
    private void loginIntegral(MemberLoginEvent memberLoginEvent) throws ParseException {
        // ??????????????????????????????
        if (checkTodayIsLogin(memberLoginEvent)) {
            return;
        }

        // ???????????????????????????
        giveTodayLoginIntegral(memberLoginEvent);
    }

    /**
     * ???????????????????????????
     *
     * @param memberLoginEvent
     */
    private void giveTodayLoginIntegral(MemberLoginEvent memberLoginEvent) {
        memberIntegralService.addIntegral(IntegralEnum.THE_DAILY_LOGIN, memberLoginEvent.getUserId(), memberLoginEvent.getUserId());
    }

    /**
     * ??????????????????????????????
     *
     * @param memberLoginEvent
     * @return
     * @throws ParseException
     */
    private boolean checkTodayIsLogin(MemberLoginEvent memberLoginEvent) throws ParseException {
        long startTime;
        long endTime;
        // ?????????????????? SimpleDateFormat ?????????????????????
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DAY);
        String format = sdf.format(new Date());
        Date parse = sdf.parse(format);
        startTime = parse.getTime() / 1000;
        endTime = startTime + 24 * 3600;
        GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
        getMemberIntegralReq.setUserId(memberLoginEvent.getUserId());
        getMemberIntegralReq.setRuleId(IntegralEnum.THE_DAILY_LOGIN.getRuleId());
        getMemberIntegralReq.setStartCreateTime(startTime);
        getMemberIntegralReq.setEndCreateTime(endTime);
        return memberIntegralService.checkExists(getMemberIntegralReq);
    }


    /**
     * ??????????????????
     *
     * @param attentionUserEvent
     */
    @EventListener
    @Async
    public void attentionMessage(AttentionUserEvent attentionUserEvent) {
        AddAttentionReq addAttentionReq = attentionUserEvent.getAddAttentionReq();
        Attention attention = attentionUserEvent.getAttention();
        Member attentionUser = memberService.memberInfo(attention.getUserId());
        String msgId = CcUtils.getUuId();
        String msg = "????????????????????? " + String.format(PROFILE_INNER_MSG, attentionUser.getUserId(), msgId, attentionUser.getUserName())
                + " ???Ta??????????????????";
        messageService.addMessage(addAttentionReq.getAttentionUserId(), msg, MessageType.SYSTEM, msgId);

        // ???????????? ????????????????????????ta
        Member member = memberService.memberInfo(addAttentionReq.getAttentionUserId());
        String emailTitle = "?????????????????????";
        String emailContent = "??????" + String.format(PROFILE_EMAIL, attentionUser.getUserId(), msgId, attentionUser.getUserName()) + " ???Ta??????????????????";
        messageService.sendEmail(member.getEmail(), emailTitle, emailContent, member.getUserId());
    }


    /**
     * ?????????????????????
     *
     * @param memberRegisterEvent
     */
    @EventListener
    @Async
    public void registerInnerMessage(MemberRegisterEvent memberRegisterEvent) {
        // ???????????????
        String msg = "<div class='text-primary' style='font-size: 16px;'>??????: ????????????????????????</div>" +
                "<div>????????????Upupor</div>" +
                "<div>?????????????????????: <a class='cv-link' data-toggle='modal' data-target='#wechat'>Upupor</a></div>" +
                "<div>????????????: <a class='cv-link' data-toggle='modal' data-target='#weibo'>UpuporCom</a></div>";
        String msgId = CcUtils.getUuId();
        messageService.addMessage(memberRegisterEvent.getUserId(), msg, MessageType.SYSTEM, msgId);
    }
}
