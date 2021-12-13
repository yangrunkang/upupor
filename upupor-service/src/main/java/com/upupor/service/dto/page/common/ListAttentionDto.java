package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Attention;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注对象
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 23:31
 */
@Data
public class ListAttentionDto extends BaseListDto {

    private List<Attention> attentionList;

    public ListAttentionDto(PageInfo pageInfo) {
        super(pageInfo);
        this.attentionList = new ArrayList<>();

    }

    public ListAttentionDto() {
        this.attentionList = new ArrayList<>();
    }
}
