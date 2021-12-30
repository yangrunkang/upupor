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

import com.upupor.service.business.aggregation.TodoAggregateService;
import com.upupor.service.common.CcConstant;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.SeoKey;
import static com.upupor.service.common.CcConstant.TODO_INDEX;


/**
 * 待办跳转
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/22 23:23
 */
@Api(tags = "Todo")
@RestController
@RequiredArgsConstructor
public class TodoPageJumpController {

    private final TodoAggregateService todoAggregateService;

    @ApiOperation("待办")
    @GetMapping("/todo-list")
    public ModelAndView todoList(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        String userId = ServletUtils.getUserId();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TODO_INDEX);
        modelAndView.addObject(todoAggregateService.index(userId, pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "待办");
        modelAndView.addObject(SeoKey.DESCRIPTION, "待办");
        return modelAndView;
    }


}
