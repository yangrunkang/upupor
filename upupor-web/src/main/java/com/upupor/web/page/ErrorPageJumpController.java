package com.upupor.web.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.service.common.CcConstant.*;


/**
 * 错误页面跳转
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/01 00:10
 */
@Slf4j
@Api(tags = "错误页面跳转")
@RestController
@RequiredArgsConstructor
public class ErrorPageJumpController {

    @ApiOperation("404页面")
    @GetMapping(URL_404)
    public ModelAndView url404() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(PAGE_404);
        modelAndView.addObject(SeoKey.TITLE, "页面不见了");
        return modelAndView;
    }

    @ApiOperation("500页面")
    @GetMapping(URL_500)
    public ModelAndView url500() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(PAGE_500);
        modelAndView.addObject(SeoKey.TITLE, "服务器内部错误");
        return modelAndView;
    }


}
