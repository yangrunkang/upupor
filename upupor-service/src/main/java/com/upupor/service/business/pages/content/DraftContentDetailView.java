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

package com.upupor.service.business.pages.content;

import com.upupor.framework.CcConstant;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.business.content.UnpublishedContent;
import com.upupor.service.business.pages.AbstractView;
import com.upupor.service.dto.page.ContentIndexDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.upupor.framework.CcConstant.ContentView.CONTENT_INDEX;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年02月09日 22:55
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class DraftContentDetailView extends AbstractView {
    public static final String URL = "/m/{contentId}";
    private final UnpublishedContent unpublishedContent;
    @Override
    public String viewName() {
        return CONTENT_INDEX;
    }

    @Override
    protected String pageUrl() {
        return URL;
    }
    @Override
    public String adapterUrlToViewName(String pageUrl) {
        if (pageUrl.startsWith("/m/")) {
            return viewName();
        }
        return pageUrl;
    }

    @Override
    protected void seoInfo() {
        for (Object value : modelAndView.getModelMap().values()) {
            if (value instanceof ContentIndexDto) {
                ContentIndexDto contentIndexDto = (ContentIndexDto) value;
                Content content = contentIndexDto.getContent();

                modelAndView.addObject(CcConstant.SeoKey.TITLE, content.getTitle());
                modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, content.getTitle());
                break;
            }
        }

    }

    @Override
    protected void fetchData() {
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        String contentId = query.getContentId();

        ContentIndexDto contentIndexDto = unpublishedContent.pageContentIndexDto(contentId, pageNum, pageSize);
        modelAndView.addObject(contentIndexDto);
    }
}
