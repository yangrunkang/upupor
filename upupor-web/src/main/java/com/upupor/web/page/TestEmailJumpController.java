/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.web.page;

import com.upupor.service.common.CcTemplateConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


/**
 * 测试邮件
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 16:26
 */
@Api(tags = "邮件模板")
@RestController
@RequiredArgsConstructor
public class TestEmailJumpController {


    @ApiOperation("邮件模板")
    @GetMapping("/email/{emailTemplate}")
    public ModelAndView emailTemplate(@PathVariable("emailTemplate") String emailTemplate) {

        String templateDic = "email/";
        String viewName = templateDic + emailTemplate;

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject(CcTemplateConstant.TITLE, "测试验证码CodeContent");
        modelAndView.addObject(CcTemplateConstant.CONTENT, "测试验证码Code");


        modelAndView.setViewName(viewName);
        return modelAndView;
    }


}
