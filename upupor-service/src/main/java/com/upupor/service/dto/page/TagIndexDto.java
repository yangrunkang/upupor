package com.upupor.service.dto.page;

import com.upupor.service.dto.page.common.ListContentDto;
import lombok.Data;

/**
 * Tag搜索页
 *
 * @author: cruise
 * @created: 2020/08/06 09:22
 */
@Data
public class TagIndexDto {
    private ListContentDto listContentDto;

    private String tagName;

    public TagIndexDto(String tagName) {
        this.listContentDto = new ListContentDto();
        this.tagName = tagName;
    }
}
