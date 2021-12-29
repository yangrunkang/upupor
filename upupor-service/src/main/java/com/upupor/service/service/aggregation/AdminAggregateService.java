package com.upupor.service.service.aggregation;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.service.aggregation.service.ContentService;
import com.upupor.service.service.aggregation.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.upupor.service.utils.ServletUtils.getUserId;

/**
 * 用户聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 23:54
 */
@Service
@RequiredArgsConstructor
public class AdminAggregateService {

    private final MemberService memberService;
    private final ContentService contentService;


    public MemberIndexDto admin(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();
        Member member = memberService.memberInfoData(userId);
        if (!member.getIsAdmin().equals(CcEnum.MemberIsAdmin.ADMIN.getStatus())) {
            throw new BusinessException(ErrorCode.USER_NOT_ADMIN);
        }
        memberIndexDto.setMember(member);
        return memberIndexDto;
    }


    public ContentIndexDto adminContent(Integer pageNum, Integer pageSize, String contentId) {
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        ContentIndexDto contentIndexDto = new ContentIndexDto();
        contentIndexDto.setContent(content);
        return contentIndexDto;
    }

}
