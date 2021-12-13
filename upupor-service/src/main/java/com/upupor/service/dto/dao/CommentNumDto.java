package com.upupor.service.dto.dao;

import lombok.Data;

/**
 * 评论数Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/21 17:30
 */
@Data
public class CommentNumDto {
    private String contentId;
    private Integer total;
}
