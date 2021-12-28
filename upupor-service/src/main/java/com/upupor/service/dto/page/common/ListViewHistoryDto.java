package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.ViewHistory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览记录
 *
 * @author cruise
 * @createTime 2021-12-28 18:03
 */
@Data
public class ListViewHistoryDto extends BaseListDto {

    private List<ViewHistory> viewHistoryList;

    public ListViewHistoryDto(PageInfo pageInfo) {
        super(pageInfo);
        this.viewHistoryList = new ArrayList<>();
    }

    public ListViewHistoryDto() {
        this.viewHistoryList = new ArrayList<>();
    }
}
