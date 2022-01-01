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

package com.upupor.service.business.viewhistory;


import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.types.ViewTargetType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象浏览记录
 *
 * @author cruise
 * @createTime 2021-12-28 18:45
 */
public abstract class AbstractViewHistory<T> {

    protected List<ViewHistory> viewHistoryList;

    protected List<ViewHistory> getViewHistoryList() {
        return viewHistoryList;
    }

    protected List<String> getViewHistoryTargetIdList() {
        return viewHistoryList.stream().map(ViewHistory::getTargetId).collect(Collectors.toList());
    }

    /**
     * 浏览类型
     */
    public abstract ViewTargetType viewTargetType();

    /**
     * 获取目标内容
     */
    public abstract List<T> getTargetList();

    /**
     * 获取用户浏览历史
     * @return
     */
    public void initData(List<ViewHistory> viewHistoryList) {
        this.viewHistoryList = viewHistoryList;
    }

    /**
     * 获取指定类型的浏览记录
     *
     * @return
     */
    public List<ViewHistory> getSpecifyViewHistory() {
        return getViewHistoryList().stream()
                .filter(s -> s.getTargetType().equals(viewTargetType())).collect(Collectors.toList());
    }

    /**
     * 设定浏览历史标题和Url
     */
    public abstract void setViewHistoryTitleAndUrl();

}
