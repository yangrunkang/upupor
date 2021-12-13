package com.upupor.service.listener.event;

import com.upupor.service.dao.entity.Member;
import lombok.Data;

/**
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:32
 */
@Data
public class MemberRegisterEvent {

    private Member member;

}
