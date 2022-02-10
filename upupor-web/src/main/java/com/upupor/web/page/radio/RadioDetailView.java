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

package com.upupor.web.page.radio;

import com.upupor.framework.CcConstant;
import com.upupor.service.business.aggregation.RadioAggregateService;
import com.upupor.service.business.aggregation.service.CommentService;
import com.upupor.service.business.aggregation.service.ViewerService;
import com.upupor.service.dto.page.RadioIndexDto;
import com.upupor.service.types.ViewTargetType;
import com.upupor.service.utils.PageUtils;
import com.upupor.web.page.abstracts.AbstractView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.upupor.framework.CcConstant.Page.SIZE_COMMENT;
import static com.upupor.framework.CcConstant.RADIO_STATION_INDEX;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年02月10日 17:01
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class RadioDetailView  extends AbstractView {
    private final CommentService commentService;
    private final ViewerService viewerService;
    private final RadioAggregateService radioAggregateService;


    public static final String URL = "/r/{radioId}";

    @Override
    public String viewName() {
        return RADIO_STATION_INDEX;
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
                modelAndView.addObject(CcConstant.SeoKey.TITLE, detail.getRadio().getRadioIntro());
                modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, detail.getRadio().getRadioIntro());
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
            pageNum = PageUtils.calcMaxPage(count, SIZE_COMMENT);
            query.setPageNum(pageNum);
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE_COMMENT;
            query.setPageSize(pageSize);
        }
    }
}
