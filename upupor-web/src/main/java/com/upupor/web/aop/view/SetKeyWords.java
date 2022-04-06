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

package com.upupor.web.aop.view;

import com.upupor.framework.CcConstant;
import com.upupor.service.utils.CcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;

import static com.upupor.framework.CcConstant.ContentView.CONTENT_INDEX;

/**
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@Service
@RequiredArgsConstructor
@Order(6)
public class SetKeyWords implements PrepareData {
    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        if (!CollectionUtils.isEmpty(model)) {
            Object o = model.get(CcConstant.SeoKey.DESCRIPTION);
            if (o instanceof String) {
                // 视图名称是文章详情,则不设置keywords,由文章详情自己返回
                String viewName = modelAndView.getViewName();
                if (Objects.nonNull(viewName) && viewName.equals(CONTENT_INDEX)) {
                    return;
                }

                String description = (String) o;
                String segmentResult = CcUtils.getSegmentResult(description);
                modelAndView.addObject(CcConstant.SeoKey.KEYWORDS, segmentResult);
            }
        }
    }

}
