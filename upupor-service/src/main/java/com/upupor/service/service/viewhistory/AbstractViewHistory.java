package com.upupor.service.service.viewhistory;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.ViewHistory;

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
    public abstract CcEnum.ViewTargetType viewTargetType();

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
                .filter(s -> s.getTargetType().equals(viewTargetType().getType())).collect(Collectors.toList());
    }

    /**
     * 设定浏览历史标题和Url
     */
    public abstract void setViewHistoryTitleAndUrl();

}
