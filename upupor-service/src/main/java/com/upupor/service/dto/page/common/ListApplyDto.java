package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Apply;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请列表Dto * * @author YangRunkang(cruise) * @date 2020/01/24 20:30
 */
@Data
public class ListApplyDto extends BaseListDto {
    private List<Apply> applyList;


    public ListApplyDto(PageInfo pageInfo) {
        super(pageInfo);
        this.applyList = new ArrayList<>();
    }

    public ListApplyDto() {
        this.applyList = new ArrayList<>();
    }

}
