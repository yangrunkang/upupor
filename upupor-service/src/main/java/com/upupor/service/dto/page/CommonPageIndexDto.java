package com.upupor.service.dto.page;

import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dto.page.common.ListBannerDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.aggregation.CommonAggregateService;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 公共页面(各个菜单的首页)
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 22:36
 */
@Data
public class CommonPageIndexDto {
    /**
     * 左边的栏目
     */
    private List<Tag> tagList;

    /**
     * 文章列表
     */
    private ListContentDto listContentDto;

    /**
     * 当前根url
     */
    private String currentRootUrl;

    /**
     * 活跃用户
     */
    private List<Member> memberList;

    /**
     * 是否领取过今天积分
     */
    private Boolean isGetDailyPoints;

    /**
     * Banner(推荐)
     */
    private ListBannerDto listBannerDto;

    /**
     * 根据文章类型来创建内容
     */
    private CommonAggregateService.HrefDesc createContentDesc;

    public CommonPageIndexDto() {
        this.listContentDto = new ListContentDto();
        this.tagList = new ArrayList<>();
    }

    public ListContentDto getListContentDto() {
        if (!CollectionUtils.isEmpty(listContentDto.getContentList())) {
            boolean exists = listContentDto.getContentList().parallelStream().anyMatch(content -> content.getContentId().equals(CcConstant.GoogleAd.FEED_AD));
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
        return listContentDto;
    }

}
