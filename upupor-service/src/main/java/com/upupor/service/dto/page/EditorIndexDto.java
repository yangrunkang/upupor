package com.upupor.service.dto.page;

import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Tag;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑器页面Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/07 00:21
 */
@Data
public class EditorIndexDto {

    /**
     * 来源
     */
    private Integer contentType;

    /**
     * 须知
     * 主要是写一些注意事项或者建议
     */
    private String instructions;

    /**
     * 特性 编辑器页面的特性
     */
    private String features;

    /**
     * 所有的标签
     */
    private List<Tag> tagList;

    /**
     * 创建文章-指定的Tag
     */
    private String createTag;

    /**
     * 文章
     */
    private Content content;

    /**
     * 根据当前用户点击的文章类型来创建内容
     */
    private CommonAggregateService.HrefDesc createContentDesc;

    public EditorIndexDto() {
        this.tagList = new ArrayList<>();
        this.content = new Content();
    }
}
