package com.upupor.service.dto.dao;

import lombok.Data;

/**
 * 最热文章Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/16 22:49
 */
@Data
public class HottestContentDto {

    /**
     * 文章Id
     */
    private String contentId;

    /**
     * 文章数据总和数据
     */
    private Integer total;

}
