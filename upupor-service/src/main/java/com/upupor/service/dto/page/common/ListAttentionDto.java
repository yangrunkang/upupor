package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Attention;
import com.upupor.service.dao.entity.Member;
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
    private List<Member> memberList;

    public ListAttentionDto(PageInfo pageInfo) {
        super(pageInfo);
        this.attentionList = new ArrayList<>();
        this.memberList = new ArrayList<>();
    }

    public ListAttentionDto() {
        this.attentionList = new ArrayList<>();
        this.memberList = new ArrayList<>();
    }
}
