package com.upupor.service.business.profile;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.business.aggregation.service.AttentionService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.common.ListAttentionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:02
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class AttentionProfile extends ProfileAbstract {
    private final AttentionService attentionService;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_ATTENTION;
    }

    @Override
    protected void setSpecifyData(String userId, Integer pageNum, Integer pageSize) {
        ListAttentionDto listAttentionDto = attentionService.getAttentions(userId, pageNum, pageSize);
        getMemberIndexDto().setListAttentionDto(listAttentionDto);
    }

    @Override
    protected void addAd() {
        ListAttentionDto listAttentionDto = getMemberIndexDto().getListAttentionDto();
        AbstractAd.ad(listAttentionDto.getMemberList());
    }
}
