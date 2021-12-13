package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Collect;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏对象Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 15:12
 */
@Data
public class ListCollectDto extends BaseListDto {

    private List<Collect> collectList;

    public ListCollectDto(PageInfo pageInfo) {
        super(pageInfo);
        this.collectList = new ArrayList<>();
    }

    public ListCollectDto() {
        this.collectList = new ArrayList<>();
    }
}
