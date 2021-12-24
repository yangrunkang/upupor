package com.upupor.service.manage.business;

import com.upupor.service.dao.entity.Attention;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.page.common.ListAttentionDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.AttentionService;
import com.upupor.service.service.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class AttentionManage extends AbstractManageInfoGet {

    @Resource
    private AttentionService attentionService;

    @Resource
    private MemberService memberService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        String userId = manageDto.getUserId();
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();

        ListAttentionDto listAttentionDto = attentionService.getAttentions(userId, pageNum, pageSize);

        List<Attention> attentionList = listAttentionDto.getAttentionList();
        if (!CollectionUtils.isEmpty(attentionList)) {

            bindAttentionMemberInfo(attentionList);
        }


        getMemberIndexDto().setListAttentionDto(listAttentionDto);

    }

    /**
     * 封装关注者 粉丝信息
     *
     * @param attentionList
     */
    private void bindAttentionMemberInfo(List<Attention> attentionList) {
        List<String> attentionUserIdList = attentionList.stream().map(Attention::getAttentionUserId).distinct().collect(Collectors.toList());
        List<Member> memberList = memberService.listByUserIdList(attentionUserIdList);

        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (Attention attention : attentionList) {
            for (Member member : memberList) {
                if (attention.getAttentionUserId().equals(member.getUserId())) {
                    attention.setMember(member);
                }
            }
        }
    }
}
