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

package com.upupor.web.page.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.framework.CcConstant.*;


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
