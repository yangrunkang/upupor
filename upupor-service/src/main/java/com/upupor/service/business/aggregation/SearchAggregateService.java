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

package com.upupor.service.business.aggregation;

import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.dto.page.SearchIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

import static com.upupor.service.common.ErrorCode.CONTENT_NOT_EXISTS;

/**
 * 搜索聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/26 13:33
 */
@Service
@RequiredArgsConstructor
public class SearchAggregateService {

    private final ContentService contentService;

    public SearchIndexDto index(String keyword, Integer pageNum, Integer pageSize) {
        SearchIndexDto searchIndexDto = new SearchIndexDto();

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setNavbarSearch(keyword);
        listContentReq.setPageNum(pageNum);
        listContentReq.setPageSize(pageSize);

        ListContentDto listContentDto = null;
        try {
            listContentDto = contentService.listContentByTitleAndShortContent(listContentReq);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                if (businessException.getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                    listContentDto = new ListContentDto();
                }
            } else {
                e.printStackTrace();
            }
        }

        if (Objects.nonNull(listContentDto)) {
            if (!CollectionUtils.isEmpty(listContentDto.getContentList())) {
                contentService.bindContentData(listContentDto.getContentList());
            }
        }

        searchIndexDto.setListContentDto(listContentDto);
        return searchIndexDto;
    }

}
