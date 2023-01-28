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

package com.upupor.service.aggregation;

import com.upupor.data.dto.page.SearchIndexDto;
import com.upupor.data.dto.page.search.SearchDataDto;
import com.upupor.lucene.UpuporLuceneService;
import com.upupor.lucene.dto.ContentFieldAndSearchDto;
import com.upupor.lucene.dto.LuceneQueryResultDto;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.SearchType;
import com.upupor.service.business.search.AbstractSearchResultRender;
import com.upupor.service.business.search.comparator.SearchDataDtoCreateTimeComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final List<AbstractSearchResultRender<?>> renderHandlerList;


    public SearchIndexDto index(String keyword) {
        SearchIndexDto searchIndexDto = new SearchIndexDto();
        List<SearchDataDto> searchDataDtoList = new ArrayList<>();

        // 搜索Lucene
        LuceneQueryResultDto luceneQueryResultDto = upuporLuceneService.searchTitle(ContentFieldAndSearchDto.TITLE, keyword, SearchType.LIKE);

        // 获取搜索结果
        List<LuceneQueryResultDto.Data> resultList = luceneQueryResultDto.getResultList();

        // 结果为空,则返回
        if (CollectionUtils.isEmpty(resultList)) {
            searchIndexDto.setSearchDataDtoList(searchDataDtoList);
            return searchIndexDto;
        }

        Map<LuceneDataType, List<LuceneQueryResultDto.Data>> resultMap = resultList.stream().collect(Collectors.groupingBy(LuceneQueryResultDto.Data::getLuceneDataType));

        for (AbstractSearchResultRender<?> render : renderHandlerList) {
            for (LuceneDataType luceneDataType : resultMap.keySet()) {
                if (luceneDataType.equals(render.dataType())) {
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

        // 关键字加红
        for (SearchDataDto searchDataDto : searchDataDtoList) {
            String replace = delHTMLTag(searchDataDto.getResultTitle()).replaceAll(keyword, "<span style='color:red'>" + keyword + "</span>");
            searchDataDto.setResultTitle(replace);
        }

        searchIndexDto.setTotal((long) searchDataDtoList.size()); // DB处理内容会影响该数字,所以使用实际元素大小
        searchIndexDto.setSearchDataDtoList(searchDataDtoList.stream().sorted(new SearchDataDtoCreateTimeComparator()).collect(Collectors.toList()));
        return searchIndexDto;
    }

    /**
     * 过滤html标签
     *
     * @param htmlStr
     * @return
     */
    public synchronized static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

}
