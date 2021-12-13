package com.upupor.service.dao.entity;

import lombok.Data;

/**
 * 文章数据
 */
@Data
public class ContentData {
    private Long id;

    private Integer viewNum;

    private Integer likeNum;

    private String contentId;

    private Integer commentNum;

}