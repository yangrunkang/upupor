package com.upupor.service.listener;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.listener.event.MemberLoginEvent;
import com.upupor.service.service.MemberIntegralService;
import com.upupor.service.service.MemberService;
import com.upupor.spi.req.GetMemberIntegralReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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
        recordLastLoginTime(memberLoginEvent);
    }

    /**
     * 记录最新登录时间
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


}
