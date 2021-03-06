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

package com.upupor.service.data.aggregation;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ๆ็ดข่ๅๆๅก
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

        // ๆ็ดขLucene
        LucenuQueryResultDto lucenuQueryResultDto = upuporLuceneService.searchTitle(ContentFieldAndSearchDto.TITLE, keyword, SearchType.LIKE);

        // ่ทๅๆ็ดข็ปๆ
        List<LucenuQueryResultDto.Data> resultList = lucenuQueryResultDto.getResultList();

        // ็ปๆไธบ็ฉบ,ๅ่ฟๅ
        if (CollectionUtils.isEmpty(resultList)) {
            searchIndexDto.setSearchDataDtoList(searchDataDtoList);
            return searchIndexDto;
        }

        Map<LuceneDataType, List<LucenuQueryResultDto.Data>> resultMap = resultList.stream().collect(Collectors.groupingBy(LucenuQueryResultDto.Data::getLuceneDataType));

        for (AbstractSearchResultRender<?> render : renderHandlerList) {
            for (LuceneDataType luceneDataType : resultMap.keySet()) {
                if(luceneDataType.equals(render.dataType())){
                    // ๅๅงๅ
                    render.init(resultMap.get(luceneDataType));
                    // ๆธฒๆ็ปๆ
                    render.renderSearchResult(luceneDataType);
                    // ๅฐ็ปๆๆพๅฐ้ๅ็ป้กต้ขไฝฟ็จ
                    searchDataDtoList.addAll(render.getSearchDataDtoList());
                    break;
                }
            }

        }

        // ๅณ้ฎๅญๅ?็บข
        for (SearchDataDto searchDataDto : searchDataDtoList) {
            String replace = delHTMLTag(searchDataDto.getResultTitle()).replaceAll(keyword, "<span style='color:red'>" + keyword + "</span>");
            searchDataDto.setResultTitle(replace);
        }

//        searchIndexDto.setTotal(lucenuQueryResultDto.getTotal());
        searchIndexDto.setTotal((long) searchDataDtoList.size()); // DBๅค็ๅๅฎนไผๅฝฑๅ่ฏฅๆฐๅญ,ๆไปฅไฝฟ็จๅฎ้ๅ็ด?ๅคงๅฐ
        searchIndexDto.setSearchDataDtoList(searchDataDtoList);
        return searchIndexDto;
    }
    /**
     * ่ฟๆปคhtmlๆ?็ญพ
     * @param htmlStr
     * @return
     */
    public synchronized static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //ๅฎไนscript็ๆญฃๅ่กจ่พพๅผ
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //ๅฎไนstyle็ๆญฃๅ่กจ่พพๅผ
        String regEx_html="<[^>]+>"; //ๅฎไนHTMLๆ?็ญพ็ๆญฃๅ่กจ่พพๅผ

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //่ฟๆปคscriptๆ?็ญพ

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //่ฟๆปคstyleๆ?็ญพ

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //่ฟๆปคhtmlๆ?็ญพ

        return htmlStr.trim(); //่ฟๅๆๆฌๅญ็ฌฆไธฒ
    }

}
