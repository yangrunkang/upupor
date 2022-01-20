package com.upupor.web.aop.view;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.framework.CcConstant.RESPONSE_TIME;
import static com.upupor.framework.utils.CcDateUtil.getResponseTime;

/**
 * 设定响应时间
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@RequiredArgsConstructor
@Service
@Order(10)
public class SetResponseTime implements PrepareData {

    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        long startTime = viewData.getStartTime();

        // 设定响应时间
        modelAndView.addObject(RESPONSE_TIME, getResponseTime(startTime));
    }

}
