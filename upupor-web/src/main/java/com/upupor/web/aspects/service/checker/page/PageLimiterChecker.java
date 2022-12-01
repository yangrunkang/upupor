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

package com.upupor.web.aspects.service.checker.page;

import com.upupor.security.limiter.AbstractLimiter;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.Limiter;
import com.upupor.web.aspects.service.checker.page.dto.PageCheckDto;
import com.upupor.web.page.view_data.business.SearchView;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.upupor.security.limiter.LimiterConstant.INTERVAL_10S;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-23 22:44
 * @email: yangrunkang53@gmail.com
 */
@Service
@Order(0)
@RequiredArgsConstructor
public class PageLimiterChecker extends AbstractLimiter implements PageAspectChecker {

    private static final List<Limiter> pageLimiterList = new ArrayList<>();

    static {
        // 搜索限制器
        pageLimiterList.add(Limiter.builder().limitType(LimitType.GLOBAL_SEARCH).frequency(3).withinSeconds(INTERVAL_10S).pageUrl(SearchView.URL).build());
    }


    @Override
    public void check(PageCheckDto pageCheckDto) {

        // 如果需要登录,就使用用户id,否则使用sessionId
        String sessionId = pageCheckDto.getRequest().getSession().getId();

        String pageUrl = pageCheckDto.getRequest().getServletPath();
        pageLimiterList.forEach(limiter -> {
            if (limiter.getPageUrl().equals(pageUrl)) {
                initPageLimiter(sessionId, limiter);
                limit();
            }
        });
    }

}
