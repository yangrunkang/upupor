package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Radio;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YangRunkang(cruise)
 * @date 2020/11/15 23:09
 */
@Data
public class ListRadioDto extends BaseListDto {

    private List<Radio> radioList;


    public ListRadioDto(PageInfo pageInfo) {
        super(pageInfo);
        this.radioList = new ArrayList<>();
    }

    public ListRadioDto() {
        this.radioList = new ArrayList<>();
    }

}
