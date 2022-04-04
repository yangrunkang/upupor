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

import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.SearchType;
import com.upupor.lucene.UpuporLuceneService;
import com.upupor.lucene.dto.ContentFieldAndSearchDto;
import com.upupor.lucene.dto.LucenuQueryResultDto;
import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.dao.entity.Radio;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.RadioService;
import com.upupor.service.dto.page.SearchIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.dto.page.search.SearchDataDto;
import lombok.RequiredArgsConstructor;
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

    private final ContentService contentService;
    private final RadioService radioService;
    private final MemberService memberService;
    private final UpuporLuceneService upuporLuceneService;


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

        // 封装数据
        List<Content> contentList = new ArrayList<>();
        List<Radio> radioList = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();
        Map<LuceneDataType, List<LucenuQueryResultDto.Data>> collect = resultList.stream().collect(Collectors.groupingBy(LucenuQueryResultDto.Data::getLuceneDataType));
        for (LuceneDataType luceneDataType : collect.keySet()) {
            List<String> targetIdList = collect.get(luceneDataType).stream().map(LucenuQueryResultDto.Data::getTarget).distinct().collect(Collectors.toList());
            if (LuceneDataType.CONTENT.equals(luceneDataType)) {
                contentList = contentService.listByContentIdList(targetIdList);
                contentService.bindContentMember(contentList);
            } else if (LuceneDataType.RADIO.equals(luceneDataType)) {
                radioList = radioService.listByRadioId(targetIdList);
                radioService.bindRadioMember(radioList);
            } else if (LuceneDataType.MEMBER.equals(luceneDataType)) {
                memberList = memberService.listByUserIdList(targetIdList);
            }
        }

        // 依次组装数据
        for (LucenuQueryResultDto.Data data : resultList) {
            if (LuceneDataType.CONTENT.equals(data.getLuceneDataType())) {
                for (Content content : contentList) {
                    if(content.getContentId().equals(data.getTarget())){
                        SearchDataDto searchDataDto = new SearchDataDto();
                        searchDataDto.setDataType(data.getLuceneDataType());
                        searchDataDto.setResultTitle(content.getTitle());
                        searchDataDto.setResultId(content.getContentId());
                        searchDataDto.setMember(content.getMember());
                        searchDataDtoList.add(searchDataDto);
                    }
                }
            } else if (LuceneDataType.RADIO.equals(data.getLuceneDataType())) {
                for (Radio radio : radioList) {
                    if(radio.getRadioId().equals(data.getTarget())){
                        SearchDataDto searchDataDto = new SearchDataDto();
                        searchDataDto.setDataType(data.getLuceneDataType());
                        searchDataDto.setResultTitle(radio.getRadioIntro());
                        searchDataDto.setResultId(radio.getRadioId());
                        searchDataDto.setMember(radio.getMember());
                        searchDataDtoList.add(searchDataDto);
                    }
                }
            } else if (LuceneDataType.MEMBER.equals(data.getLuceneDataType())) {
                for (Member member : memberList) {
                    if(member.getUserId().equals(data.getTarget())){
                        SearchDataDto searchDataDto = new SearchDataDto();
                        searchDataDto.setDataType(data.getLuceneDataType());
                        searchDataDto.setResultTitle(member.getUserName());
                        searchDataDto.setResultId(member.getUserId());
                        searchDataDto.setMember(member);
                        searchDataDtoList.add(searchDataDto);
                    }
                }
            }
        }

        searchIndexDto.setTotal(lucenuQueryResultDto.getTotal());
        searchIndexDto.setSearchDataDtoList(searchDataDtoList);
        return searchIndexDto;
    }


    private abstract class AbstractRenderData {

    }

}
