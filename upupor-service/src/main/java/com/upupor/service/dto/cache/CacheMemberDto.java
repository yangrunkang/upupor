package com.upupor.service.dto.cache;

import com.upupor.service.dao.entity.Member;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存用户类
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/25 12:52
 */
@Data
public class CacheMemberDto {

    private List<Member> memberList;

    public CacheMemberDto() {
        this.memberList = new ArrayList<>();
    }
}
