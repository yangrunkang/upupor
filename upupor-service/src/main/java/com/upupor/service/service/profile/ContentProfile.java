package com.upupor.service.service.profile;

import com.upupor.service.business.AdService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.aggregation.service.ContentService;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:01
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class ContentProfile extends ProfileAbstract {
    private final ContentService contentService;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_CONTENT;
    }

    @Override
    protected void setSpecifyData(String userId, Integer pageNum, Integer pageSize) {

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setUserId(userId);
        listContentReq.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());
        listContentReq.setPageNum(pageNum);
        listContentReq.setPageSize(pageSize);
        ListContentDto listContentDto = contentService.listContent(listContentReq);
        // 绑定文章数据
        contentService.bindContentData(listContentDto);
        getMemberIndexDto().setListContentDto(listContentDto);
    }

    @Override
    protected void addAd() {
        ListContentDto listContentDto = getMemberIndexDto().getListContentDto();
        AdService.contentListAd(listContentDto.getContentList());

    }
}
