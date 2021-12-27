package com.upupor.service.service.aggregation.abstracts.impl;

import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.aggregation.abstracts.ProfileAbstract;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Random;

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
        // 个人主页文章列表添加广告
        if (!CollectionUtils.isEmpty(listContentDto.getContentList())) {
            boolean exists = listContentDto.getContentList().parallelStream().anyMatch(t -> t.getContentId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = listContentDto.getContentList().size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Content adContent = new Content();
                adContent.setContentId(CcConstant.GoogleAd.FEED_AD);
                adContent.setUserId(CcConstant.GoogleAd.FEED_AD);
                listContentDto.getContentList().add(adIndex, adContent);
            }
        }

    }
}
