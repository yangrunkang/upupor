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

import com.upupor.lucene.UpuporLuceneService;
import com.upupor.lucene.dto.ContentFieldAndSearchDto;
import com.upupor.lucene.dto.LucenuQueryResultDto;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.SearchType;
import com.upupor.service.business.search.AbstractSearchResultRender;
import com.upupor.service.dto.page.SearchIndexDto;
import com.upupor.service.dto.page.search.SearchDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/26 13:33
 */
@Service
@RequiredArgsConstructor
public class SearchAggregateService {

    private final UpuporLuceneService upuporLuceneService;
    private final List<AbstractSearchResultRender> renderHandlerList;


    public SearchIndexDto index(String keyword) {
        SearchIndexDto searchIndexDto = new SearchIndexDto();
        List<SearchDataDto> searchDataDtoList = new ArrayList<>();

        // 搜索Lucene
        LucenuQueryResultDto lucenuQueryResultDto = upuporLuceneService.searchTitle(ContentFieldAndSearchDto.TITLE, keyword, SearchType.LIKE);

        // 获取搜索结果
        List<LucenuQueryResultDto.Data> resultList = lucenuQueryResultDto.getResultList();

        // 结果为空,则返回
        if (CollectionUtils.isEmpty(resultList)) {
            searchIndexDto.setSearchDataDtoList(searchDataDtoList);
            return searchIndexDto;
        }

        Map<LuceneDataType, List<LucenuQueryResultDto.Data>> resultMap = resultList.stream().collect(Collectors.groupingBy(LucenuQueryResultDto.Data::getLuceneDataType));

        for (AbstractSearchResultRender render : renderHandlerList) {
            for (LuceneDataType luceneDataType : resultMap.keySet()) {
                if(luceneDataType.equals(render.dataType())){
                    // 初始化
                    render.init(resultMap.get(luceneDataType));
                    // 渲染结果
                    render.renderSearchResult(luceneDataType);
                    // 将结果放到集合给页面使用
                    searchDataDtoList.addAll(render.getSearchDataDtoList());
                    break;
                }
            }

        }


        searchIndexDto.setTotal(lucenuQueryResultDto.getTotal());
        searchIndexDto.setSearchDataDtoList(searchDataDtoList);
        return searchIndexDto;
    }


}
