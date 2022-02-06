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

package com.upupor.web.page;

import com.upupor.framework.CcConstant;
import com.upupor.service.business.aggregation.AdminAggregateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.framework.CcConstant.*;

/**
 * 用户页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 12:16
 */
@Slf4j
@Api(tags = "用户页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class AdminPageJumpController {

    private final AdminAggregateService adminAggregateService;

    @ApiOperation("管理员-管理员")
    @GetMapping("/user/admin/admin")
    public ModelAndView userManageAdmin(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_ADMIN);
        modelAndView.addObject(adminAggregateService.admin(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "管理员");
        modelAndView.addObject(SeoKey.DESCRIPTION, "管理员");
        return modelAndView;
    }

    @ApiOperation("管理员-管理文章")
    @GetMapping("/user/admin/content/{contentId}")
    public ModelAndView userManageAdmin(Integer pageNum, Integer pageSize, @PathVariable("contentId") String contentId) {

        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_CONTENT);
        modelAndView.addObject(adminAggregateService.adminContent(pageNum, pageSize, contentId));
        modelAndView.addObject(SeoKey.TITLE, "管理员");
        modelAndView.addObject(SeoKey.DESCRIPTION, "管理员");
        return modelAndView;
    }


}
