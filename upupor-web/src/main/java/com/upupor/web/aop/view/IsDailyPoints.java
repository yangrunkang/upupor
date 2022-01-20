package com.upupor.web.aop.view;

import com.upupor.service.business.aggregation.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.framework.CcConstant.DAILY_POINTS;

/**
 * 每日签到
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@RequiredArgsConstructor
@Service
@Order(4)
public class IsDailyPoints implements PrepareData {
    private final MemberService memberService;
    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        // 今日是否签到
        modelAndView.addObject(DAILY_POINTS, memberService.checkIsGetDailyPoints());
    }
}
