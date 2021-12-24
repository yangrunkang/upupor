package com.upupor.service.manage.business;

import com.upupor.service.common.BusinessException;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.CommonService;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.manage.service.ContentManageService;
import com.upupor.service.service.ContentService;
import com.upupor.spi.req.ListContentReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.service.common.ErrorCode.CONTENT_NOT_EXISTS;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class ContentManage extends AbstractManageInfoGet {
    @Resource
    private ContentManageService contentManageService;
    @Resource
    private CommonService commonService;
    @Resource
    private ContentService contentService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();
        String searchTitle = manageDto.getSearchTitle();
        String select = manageDto.getSelect();

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setUserId(userId);
        listContentReq.setPageSize(pageSize);
        listContentReq.setPageNum(pageNum);
        listContentReq.setSearchTitle(searchTitle);
        // 筛选
        listContentReq.setSelect(select);

        ListContentDto listContentDto = null;
        try {
            listContentDto = contentManageService.listContent(listContentReq);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                if (businessException.getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                    listContentDto = new ListContentDto();
                }
            }
            e.printStackTrace();
        }
        // 处理标签
        assert listContentDto != null;
        commonService.handListContentDtoTagName(listContentDto);
        contentService.handlePinnedContent(listContentDto, userId);
        getMemberIndexDto().setListContentDto(listContentDto);

    }
}