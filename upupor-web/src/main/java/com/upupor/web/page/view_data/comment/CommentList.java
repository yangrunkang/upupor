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

package com.upupor.web.page.view_data.comment;

import com.upupor.data.dto.page.CommentIndexDto;
import com.upupor.framework.CcConstant;
import com.upupor.service.aggregation.CommentAggregateService;
import com.upupor.web.page.view_data.AbstractView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.upupor.framework.CcConstant.COMMENT_INDEX;

/**
 * 评论列表
 *
 * @author Yang Runkang (cruise)
 * @date 2023年01月09日 10:59
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class CommentList extends AbstractView {
    public static final String URL = "/comment";
    private final CommentAggregateService commentAggregateService;

    @Override
    public String viewName() {
        return COMMENT_INDEX;
    }

    @Override
    protected String pageUrl() {
        return URL;
    }

    @Override
    protected void seoInfo() {
        modelAndView.addObject(CcConstant.SeoKey.TITLE, "所有评论");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "所有评论");
        modelAndView.addObject(CcConstant.SeoKey.BUSINESS_TITLE, "所有评论");
    }

    @Override
    protected void fetchData() {
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        CommentIndexDto commentIndexDto = commentAggregateService.index(pageNum, pageSize);
        modelAndView.addObject(commentIndexDto);
    }

}