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

import com.upupor.lucene.SearchType;
import com.upupor.lucene.UpuporLuceneService;
import com.upupor.lucene.dto.ContentFieldAndSearchDto;
import com.upupor.lucene.dto.LucenuQueryResultDto;
import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.dto.page.SearchIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final UpuporLuceneService upuporLuceneService;



    public SearchIndexDto index(String keyword) {
        SearchIndexDto searchIndexDto = new SearchIndexDto();
        ListContentDto listContentDto = new ListContentDto();

        LucenuQueryResultDto lucenuQueryResultDto = upuporLuceneService.searchTitle(ContentFieldAndSearchDto.TITLE, keyword, SearchType.LIKE);

        List<LucenuQueryResultDto.Data> resultList = lucenuQueryResultDto.getResultList();

        if(CollectionUtils.isEmpty(resultList)){
            searchIndexDto.setListContentDto(listContentDto);
            return searchIndexDto;
        }

        List<String> contentIdList = resultList.stream().map(LucenuQueryResultDto.Data::getContentId).distinct().collect(Collectors.toList());
        List<Content> contentList = contentService.listByContentIdList(contentIdList);
        if (!CollectionUtils.isEmpty(contentList)) {
            contentService.bindContentData(contentList);
            // 绑定文章数据
            contentService.bindContentData(contentList);
            // 绑定用户信息
            contentService.bindContentMember(contentList);
        }


        listContentDto.setContentList(contentList);
        listContentDto.setTotal(lucenuQueryResultDto.getTotal());
        searchIndexDto.setListContentDto(listContentDto);
        return searchIndexDto;
    }


}
