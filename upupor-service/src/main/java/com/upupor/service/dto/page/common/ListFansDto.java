package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Fans;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 粉丝对象
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 15:03
 */
@Data
public class ListFansDto extends BaseListDto {
    private List<Fans> fansList;

    public ListFansDto(PageInfo pageInfo) {
        super(pageInfo);
        this.fansList = new ArrayList<>();
    }

    public ListFansDto() {
        this.fansList = new ArrayList<>();
    }
}
