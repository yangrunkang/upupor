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

package com.upupor.web.page.view_data.radio;

import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dto.page.RadioIndexDto;
import com.upupor.data.types.ViewTargetType;
import com.upupor.data.utils.PageUtils;
import com.upupor.framework.CcConstant;
import com.upupor.service.aggregation.RadioAggregateService;
import com.upupor.service.base.CommentService;
import com.upupor.service.base.ViewerService;
import com.upupor.web.page.view_data.AbstractView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.upupor.framework.CcConstant.Page.SIZE;
import static com.upupor.framework.CcConstant.RADIO_STATION_INDEX;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年02月10日 17:01
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class RadioDetailView extends AbstractView {
    private final CommentService commentService;
    private final ViewerService viewerService;
    private final RadioAggregateService radioAggregateService;


    public static final String URL = "/r/{radioId}";

    @Override
    public String viewName() {
        return RADIO_STATION_INDEX;
    }

    @Override
    protected String pageUrl() {
        return URL;
    }

    @Override
    public String adapterUrlToViewName(String pageUrl) {
        if (pageUrl.startsWith("/r/")) {
            return viewName();
        }
        return pageUrl;
    }

    @Override
    protected void seoInfo() {
        for (Object value : modelAndView.getModelMap().values()) {
            if (value instanceof RadioIndexDto) {
                RadioIndexDto detail = (RadioIndexDto) value;
                Radio radio = detail.getRadioEnhance().getRadio();
                modelAndView.addObject(CcConstant.SeoKey.TITLE, radio.getRadioIntro());
                modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, radio.getRadioIntro());
                break;
            }
        }
    }

    @Override
    protected void fetchData() {
        String radioId = query.getRadioId();
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        RadioIndexDto detail = radioAggregateService.detail(radioId, pageNum, pageSize);
        // 记录访问者
        viewerService.addViewer(radioId, ViewTargetType.RADIO);
        // 电台列表
        modelAndView.addObject(detail);
    }

    @Override
    protected void specifyPage() {
        String radioId = query.getRadioId();
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();

        if (Objects.isNull(pageNum)) {
            // 获取最新的评论
            Integer count = commentService.countByTargetId(radioId);
            pageNum = PageUtils.calcMaxPage(count, SIZE);
            query.setPageNum(pageNum);
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
            query.setPageSize(pageSize);
        }
    }


    @Override
    protected Boolean isNeedSpecifyPage() {
        return Boolean.TRUE;
    }
}
