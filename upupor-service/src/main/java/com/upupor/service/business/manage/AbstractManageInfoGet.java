package com.upupor.service.business.manage;

import com.upupor.service.business.aggregation.service.CssPatternService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.utils.ServletUtils;

import javax.annotation.Resource;

/**
 * 管理抽象
 *
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
public abstract class AbstractManageInfoGet {

    @Resource
    private MemberService memberService;

    @Resource
    private CssPatternService cssPatternService;

    /**
     * 管理统一返回的IndexDto
     */
    private MemberIndexDto memberIndexDto = new MemberIndexDto();

    /**
     * 获取用户信息
     */
    public MemberIndexDto initMemberInfo() {
        memberIndexDto = new MemberIndexDto();
        String userId =ServletUtils.getUserId();
        Member member = memberService.memberInfoData(userId);
        member.setMemberConfig(member.getMemberConfig());
        memberIndexDto.setMember(member);
        memberIndexDto.setListCssPatternDto(cssPatternService.getAll(userId));
        return memberIndexDto;
    }

    protected MemberIndexDto getMemberIndexDto() {
        return memberIndexDto;
    }

    /**
     * 指定Dto处理
     *
     * @param manageDto
     */
    protected abstract void specifyDtoHandle(ManageDto manageDto);

    /**
     * 暴露出去
     *
     * @return
     */
    public MemberIndexDto getData(ManageDto manageDto) {
        initMemberInfo();
        specifyDtoHandle(manageDto);
        return memberIndexDto;
    }


}
