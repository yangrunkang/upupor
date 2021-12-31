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

package com.upupor.service.business.content;

import com.upupor.service.dto.page.ContentIndexDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 内容数据集合
 *
 * @author runkangyang (cruise)
 * @date 2020.01.11 11:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAggregateService {

    private final PublishedContent publishedContent;
    private final UnpublishedContent unpublishedContent;

    /**
     * 文章详情
     *
     * @param contentId
     * @return
     */
    public ContentIndexDto contentDetail(String contentId, Integer pageNum, Integer pageSize) {
        return publishedContent.pageContentIndexDto(contentId, pageNum, pageSize);
    }

    /**
     * 文章详情
     *
     * @param contentId
     * @return
     */
    public ContentIndexDto contentManageDetail(String contentId, Integer pageNum, Integer pageSize) {
        return unpublishedContent.pageContentIndexDto(contentId, pageNum, pageSize);
    }

}
