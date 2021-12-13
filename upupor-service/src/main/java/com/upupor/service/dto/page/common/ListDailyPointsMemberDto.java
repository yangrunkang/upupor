package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Member;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 签到用户列表
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/05 00:57
 */
@Data
public class ListDailyPointsMemberDto extends BaseListDto {

    private List<Member> memberList;

    public ListDailyPointsMemberDto(PageInfo pageInfo) {
        super(pageInfo);
        this.memberList = new ArrayList<>();
    }

    public ListDailyPointsMemberDto() {
        this.memberList = new ArrayList<>();
    }
}
