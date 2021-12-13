package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Content;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容列表(文章通用结构)
 *
 * @author: YangRunkang(cruise)
 * @created: 2020/01/05 13:21
 */
@Data
public class ListContentDto extends BaseListDto {

    /**
     * 内容集合
     */
    private List<Content> contentList;


    public ListContentDto(PageInfo pageInfo) {
        super(pageInfo);
        this.contentList = new ArrayList();
    }

    public ListContentDto() {
        this.contentList = new ArrayList();
    }

    public List<Content> getContentList() {
        if (CollectionUtils.isEmpty(contentList)) {
            return new ArrayList<>();
        }
        return contentList;
    }
}
