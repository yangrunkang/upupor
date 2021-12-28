package com.upupor.service.dao.entity;

import lombok.Data;

/**
 * 文章数据
 */
@Data
public class ContentData extends BaseEntity {

    private Integer viewNum;

    private Integer likeNum;

    private String contentId;

    private Integer commentNum;

}