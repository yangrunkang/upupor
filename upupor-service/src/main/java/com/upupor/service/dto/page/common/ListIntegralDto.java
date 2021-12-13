package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.MemberIntegral;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户积分信息
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/08 14:22
 */
@Data
public class ListIntegralDto extends BaseListDto {
    /**
     * 总积分
     */
    private Integer totalIntegral = 0;
    private List<MemberIntegral> memberIntegralList;


    public ListIntegralDto(PageInfo pageInfo) {
        super(pageInfo);
        this.memberIntegralList = new ArrayList<>();
    }

    public ListIntegralDto() {
        this.memberIntegralList = new ArrayList<>();
    }
}
