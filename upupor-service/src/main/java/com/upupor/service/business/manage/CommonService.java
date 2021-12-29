package com.upupor.service.business.manage;

import com.upupor.service.business.aggregation.service.TagService;
import com.upupor.service.dto.page.common.ListContentDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:01
 */
@Component
public class CommonService {

    @Resource
    private TagService tagService;

    public void handListContentDtoTagName(ListContentDto listContentDto) {
        if (CollectionUtils.isEmpty(listContentDto.getContentList())) {
            return;
        }

        listContentDto.getContentList().forEach(content -> {
            if (!StringUtils.isEmpty(content.getTagIds())) {
                content.setTagDtoList(tagService.listTagNameByTagId(content.getTagIds()));
            }
        });
    }
}
