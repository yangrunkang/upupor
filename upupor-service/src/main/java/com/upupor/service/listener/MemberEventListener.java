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

import com.upupor.data.dao.entity.Attention;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.types.MessageType;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.MemberIntegralService;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.MessageService;
import com.upupor.service.business.links.LinkBuilderInstance;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.MsgType;
import com.upupor.service.business.links.abstracts.dto.MemberProfileLinkParamDto;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.listener.event.AttentionUserEvent;
import com.upupor.service.listener.event.MemberLoginEvent;
import com.upupor.service.listener.event.MemberRegisterEvent;
import com.upupor.service.outer.req.AddAttentionReq;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户事件监听器
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
     * 用户登录时间
     *
     * @param memberLoginEvent
     */
    @EventListener
    @Async
    public void login(MemberLoginEvent memberLoginEvent) throws ParseException {
        // 登录送积分
        loginIntegral(memberLoginEvent);
        // 记录最新登录时间
        recordTheLatestLoginTime(memberLoginEvent);
    }

    /**
     * 记录最新登录时间
     *
     * @param memberLoginEvent
     */
    private void recordTheLatestLoginTime(MemberLoginEvent memberLoginEvent) {
        String userId = memberLoginEvent.getUserId();
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        memberService.updateMemberLoginTime(userId);
    }

    /**
     * 登录送积分
     *
     * @param memberLoginEvent
     * @throws ParseException
     */
    private void loginIntegral(MemberLoginEvent memberLoginEvent) throws ParseException {
        // 检查今天是否已经登录
        if (checkTodayIsLogin(memberLoginEvent)) {
            return;
        }

        // 赠送今天登录的积分
        giveTodayLoginIntegral(memberLoginEvent);
    }

    /**
     * 赠送今天登录的积分
     *
     * @param memberLoginEvent
     */
    private void giveTodayLoginIntegral(MemberLoginEvent memberLoginEvent) {
        memberIntegralService.addIntegral(IntegralEnum.THE_DAILY_LOGIN, memberLoginEvent.getUserId(), memberLoginEvent.getUserId());
    }

    /**
     * 检查今天是否已经登录
     *
     * @param memberLoginEvent
     * @return
     * @throws ParseException
     */
    private boolean checkTodayIsLogin(MemberLoginEvent memberLoginEvent) throws ParseException {
        long startTime;
        long endTime;
        // 局部变量解决 SimpleDateFormat 线程不安全问题
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
     * 关注消息事件
     *
     * @param attentionUserEvent
     */
    @EventListener
    @Async
    public void attentionMessage(AttentionUserEvent attentionUserEvent) {
        AddAttentionReq addAttentionReq = attentionUserEvent.getAddAttentionReq();
        Attention attention = attentionUserEvent.getAttention();
        Member attentionUser = memberService.memberInfo(attention.getUserId()).getMember();
        String msgId = CcUtils.getUuId();


        MemberProfileLinkParamDto memberProfileLinkParamDto = MemberProfileLinkParamDto.builder()
                .memberUserId(attentionUser.getUserId())
                .msgId(msgId)
                .memberUserName(attentionUser.getUserName())
                .build();
        String buildProfileMsg = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = LinkBuilderInstance.buildLink(BusinessLinkType.MEMBER_PROFILE, memberProfileLinkParamDto, MsgType.EMAIL);

        String msg = "您有新的关注者 " + buildProfileMsg + " 去Ta的主页看看吧";

        // 发送邮件 被关注的人要通知ta
        Member member = memberService.memberInfo(addAttentionReq.getAttentionUserId()).getMember();
        String emailContent = "点击" + buildProfileMsgEmail + " 去Ta的主页看看吧";

        MessageSend.send(MessageModel.builder()
                .toUserId(member.getUserId())
                .messageType(MessageType.SYSTEM)
                .emailModel(MessageModel.EmailModel.builder()
                        .title("您有新的关注者")
                        .content(emailContent)
                        .build())
                .innerModel(MessageModel.InnerModel.builder()
                        .message(msg).build())
                .messageId(msgId)
                .build());

    }


    /**
     * 新用户注册时间
     *
     * @param memberRegisterEvent
     */
    @EventListener
    @Async
    public void registerInnerMessage(MemberRegisterEvent memberRegisterEvent) {
        // 添加站内信
        String msg = "欢迎来到Upupor~  很高兴遇见你~";
        String msgId = CcUtils.getUuId();

        MessageSend.send(MessageModel.builder()
                .toUserId(memberRegisterEvent.getUserId())
                .messageType(MessageType.SYSTEM)
                .innerModel(MessageModel.InnerModel.builder()
                        .message(msg).build())
                .messageId(msgId)
                .build());

    }
}
