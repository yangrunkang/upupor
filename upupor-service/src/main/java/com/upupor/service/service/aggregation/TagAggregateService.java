package com.upupor.service.service.aggregation;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.TagIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.TagService;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: cruise
 * @created: 2020/08/06 09:21
 */
@Service
@RequiredArgsConstructor
public class TagAggregateService {
    private final ContentService contentService;

    private final TagService tagService;

    public TagIndexDto index(String tagName, Integer pageNum, Integer pageSize) {
        TagIndexDto tagIndexDto = new TagIndexDto(tagName);

        // 根据 tagName 获取tagId
        List<String> tagIdList = tagService.getTagListByName(tagName);
        if (CollectionUtils.isEmpty(tagIdList)) {
            return new TagIndexDto(tagName);
        }

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setTagIdList(tagIdList);
        listContentReq.setPageNum(pageNum);
        listContentReq.setPageSize(pageSize);
        listContentReq.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());

        ListContentDto listContentDto = contentService.listContents(listContentReq);
        tagIndexDto.setListContentDto(listContentDto);
        tagIndexDto.setTagName(tagName);
        return tagIndexDto;
    }
}
