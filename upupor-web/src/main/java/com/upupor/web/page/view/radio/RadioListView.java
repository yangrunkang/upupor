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

package com.upupor.web.page.view.radio;

import com.upupor.framework.CcConstant;
import com.upupor.service.aggregation.RadioAggregateService;
import com.upupor.web.page.view.AbstractView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.upupor.framework.CcConstant.RADIO_STATION_LIST;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年02月10日 17:00
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class RadioListView extends AbstractView {

    private final RadioAggregateService radioAggregateService;

    public static final String URL = "/radio-station";

    @Override
    public String viewName() {
        return RADIO_STATION_LIST;
    }

    @Override
    protected String pageUrl() {
        return URL;
    }

    @Override
    protected void seoInfo() {
        modelAndView.addObject(CcConstant.SeoKey.TITLE, "电台列表");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "电台,节目,分享,独立思考");
    }

    @Override
    protected void fetchData() {
        modelAndView.addObject(radioAggregateService.index(query.getPageNum(), query.getPageSize()));
    }
}
