package com.upupor.service.service.aggregation;

import com.upupor.service.common.BusinessException;
import com.upupor.service.dto.page.SearchIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.service.ContentService;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

import static com.upupor.service.common.ErrorCode.CONTENT_NOT_EXISTS;

/**
 * 搜索聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/26 13:33
 */
@Service
@RequiredArgsConstructor
public class SearchAggregateService {

    private final ContentService contentService;

    public SearchIndexDto index(String keyword, Integer pageNum, Integer pageSize) {
        SearchIndexDto searchIndexDto = new SearchIndexDto();

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setNavbarSearch(keyword);
        listContentReq.setPageNum(pageNum);
        listContentReq.setPageSize(pageSize);

        ListContentDto listContentDto = null;
        try {
            listContentDto = contentService.listContentByTitleAndShortContent(listContentReq);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                if (businessException.getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                    listContentDto = new ListContentDto();
                }
            } else {
                e.printStackTrace();
            }
        }

        if (Objects.nonNull(listContentDto)) {
            if (!CollectionUtils.isEmpty(listContentDto.getContentList())) {
                contentService.bindContentData(listContentDto.getContentList());
            }
        }

        searchIndexDto.setListContentDto(listContentDto);
        return searchIndexDto;
    }

}
