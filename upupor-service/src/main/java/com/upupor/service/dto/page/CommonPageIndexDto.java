package com.upupor.service.dto.page;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dto.page.common.ListBannerDto;
import com.upupor.service.dto.page.common.ListContentDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
        AbstractAd.ad(listContentDto.getContentList());
        return listContentDto;
    }

}
