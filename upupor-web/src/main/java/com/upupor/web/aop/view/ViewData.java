package com.upupor.web.aop.view;

import lombok.Getter;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Getter
public class ViewData {
    private ModelAndView modelAndView;
    private String servletPath;
    private long startTime;

    public static ViewData create(ModelAndView modelAndView, String servletPath, long startTime){
        ViewData viewData = new ViewData();
        viewData.modelAndView = modelAndView;
        viewData.servletPath = servletPath;
        viewData.startTime = startTime;
        return viewData;
    }

}
