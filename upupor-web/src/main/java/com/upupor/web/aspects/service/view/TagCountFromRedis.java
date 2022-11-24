/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.web.aspects.service.view;

import com.alibaba.fastjson2.JSON;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.business.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Objects;

import static com.upupor.framework.CcConstant.CV_TAG_LIST;
import static com.upupor.framework.CcRedisKey.TAG_COUNT;

/**
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@Service
@Order(8)
@RequiredArgsConstructor
public class TagCountFromRedis implements PrepareData {
    private final TaskService taskService;

    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        String s = RedisUtil.get(TAG_COUNT);
        Object result = JSON.parseObject(s, Object.class);
        if (Objects.isNull(result)) {
            modelAndView.addObject(CV_TAG_LIST, new ArrayList<>());
            // 刷新下
            taskService.refreshTag();
        }

        modelAndView.addObject(CV_TAG_LIST, result);
    }
}
