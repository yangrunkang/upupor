package com.upupor.service.dto.page.common;

import lombok.Builder;
import lombok.Data;

/**
 * 标签Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/06 20:27
 */
@Builder
@Data
public class TagDto {

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 标签id
     */
    private String tagId;

}
