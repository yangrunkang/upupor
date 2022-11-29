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

package com.upupor.service.business.search;

import com.upupor.data.dao.entity.enhance.RadioEnhance;
import com.upupor.data.dto.page.search.SearchDataDto;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.service.base.RadioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年04月04日 20:29
 * @email: yangrunkang53@gmail.com
 */
@RequiredArgsConstructor
@Component
public class RadioSearchResultRender extends AbstractSearchResultRender<RadioEnhance> {
    private final RadioService radioService;

    @Override
    public LuceneDataType dataType() {
        return LuceneDataType.RADIO;
    }

    @Override
    protected void bindMemberList(List<RadioEnhance> radioEnhanceList) {
        radioService.bindRadioMember(radioEnhanceList);
    }

    @Override
    protected List<RadioEnhance> getDataList() {
        return radioService.listByRadioId(getTargetIdList()).stream().map(s -> {
            return RadioEnhance.builder().radio(s).build();
        }).collect(Collectors.toList());
    }

    @Override
    protected void transferToSearchDataDtoList(List<RadioEnhance> radios) {
        for (RadioEnhance radioEnhance : radios) {
            SearchDataDto searchDataDto = new SearchDataDto();
            searchDataDto.setDataType(dataType());
            searchDataDto.setResultTitle(radioEnhance.getRadio().getRadioIntro());
            searchDataDto.setResultId(radioEnhance.getRadio().getRadioId());
            searchDataDto.setMemberEnhance(radioEnhance.getMemberEnhance());
            getSearchDataDtoList().add(searchDataDto);
        }

    }
}
