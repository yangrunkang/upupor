package com.upupor.web.aop.view_data;

import org.springframework.web.servlet.ModelAndView;

/**
 * 准备数据
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
public interface PrepareData {
    void prepare(ModelAndView modelAndView);
}
