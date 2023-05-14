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

package com.upupor.web.page.view_data.todo;

import com.upupor.framework.CcConstant;
import com.upupor.service.aggregation.TodoAggregateService;
import com.upupor.web.page.view_data.AbstractView;
import com.upupor.service.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.upupor.framework.CcConstant.TODO_INDEX;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年02月09日 22:52
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class TodoListView extends AbstractView {
    private final TodoAggregateService todoAggregateService;

    public static final String URL = "/todo-list";

    @Override
    public String viewName() {
        return TODO_INDEX;
    }

    @Override
    protected String pageUrl() {
        return URL;
    }

    @Override
    protected void seoInfo() {

        modelAndView.addObject(CcConstant.SeoKey.TITLE, "待办");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "待办");
    }

    @Override
    protected void fetchData() {
        String userId = SessionUtils.getUserId();
        modelAndView.addObject(todoAggregateService.index(userId, query.getPageNum(), query.getPageSize()));
    }
}
