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

package com.upupor.service.business.search;

import com.upupor.lucene.dto.LuceneQueryResultDto;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.data.dto.page.search.SearchDataDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象搜索结果渲染
 *
 * @author Yang Runkang (cruise)
 * @date 2022年04月04日 20:23
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractSearchResultRender<T> {

    @Getter
    public List<SearchDataDto> searchDataDtoList;

    @Getter
    private List<LuceneQueryResultDto.Data> resultByTypeList;

    /**
     * 数据类型
     *
     * @return
     */
    public abstract LuceneDataType dataType();

    /**
     * 获取数据集合
     *
     * @return
     */
    protected abstract List<T> getDataList();

    /**
     * @return
     */
    protected List<String> getTargetIdList() {
        return resultByTypeList.stream().filter(result -> result.getLuceneDataType().equals(dataType())).map(LuceneQueryResultDto.Data::getTarget).distinct().collect(Collectors.toList());
    }

    /**
     * 绑定数据对应的用户
     *
     * @param tList
     */
    protected void bindMemberList(List<T> tList) {

    }

    /**
     * 转换为Dto
     */
    protected abstract void transferToSearchDataDtoList(List<T> tList);

    /**
     * 初始化
     *
     * @param resultByTypeList
     */
    public void init(List<LuceneQueryResultDto.Data> resultByTypeList) {
        this.resultByTypeList = resultByTypeList;
        this.searchDataDtoList = new ArrayList<>();
    }

    /**
     * 渲染搜索结果
     *
     * @param dataType
     */
    public void renderSearchResult(LuceneDataType dataType) {
        if (dataType().equals(dataType)) {
            // 获取数据
            List<T> dataList = getDataList();
            // 绑定用户
            bindMemberList(dataList);
            // 转换
            transferToSearchDataDtoList(dataList);
        }
    }

}
