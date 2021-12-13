package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Member;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户列表
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/29 15:32
 */
@Data
public class ListMemberDto extends BaseListDto {

    private List<Member> memberList;

    public ListMemberDto(PageInfo pageInfo) {
        super(pageInfo);
        this.memberList = new ArrayList<>();
    }

    public ListMemberDto() {
        this.memberList = new ArrayList<>();
    }
}
