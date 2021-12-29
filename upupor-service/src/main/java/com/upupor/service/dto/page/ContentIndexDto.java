package com.upupor.service.dto.page;

import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ContentEditReason;
import com.upupor.service.dto.page.common.TagDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/12 01:16
 */
@Data
public class ContentIndexDto {

    /**
     * 作者其他的文章
     */
    List<Content> authorOtherContentList;
    private Content content;
    private List<TagDto> tagDtoList;
    /**
     * true:已经收藏  false:未收藏
     */
    private Boolean currUserIsCollect;
    /**
     * true:已经点赞  false:未点赞
     */
    private Boolean currUserIsClickLike;
    /**
     * true:已关注  false:未关注
     */
    private Boolean currUserIsAttention;
    /**
     * 是否有电台
     */
    private Boolean hasRadio;
    private ContentEditReason contentEditReason;

    /**
     * 推荐文章
     */
    private List<Content> randomContentList;

    /**
     * 根据当前用户点击的文章类型来创建内容
     */
    private CommonAggregateService.HrefDesc createContentDesc;

    public ContentIndexDto() {
        this.tagDtoList = new ArrayList<>();
        this.currUserIsCollect = false;
        this.currUserIsAttention = false;
        this.hasRadio = false;
        this.authorOtherContentList = new ArrayList<>();
        this.randomContentList = new ArrayList<>();
    }
}
