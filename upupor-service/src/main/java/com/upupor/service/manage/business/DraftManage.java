package com.upupor.service.manage.business;

import com.upupor.service.common.BusinessException;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.CommonService;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.manage.service.ContentManageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.service.common.ErrorCode.CONTENT_NOT_EXISTS;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class DraftManage extends AbstractManageInfoGet {

    @Resource
    private ContentManageService contentManageService;

    @Resource
    private CommonService commonService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();
        String searchTitle = manageDto.getSearchTitle();

        ListContentDto listContentDto = null;
        try {
            listContentDto = contentManageService.listContentDraft(pageNum, pageSize, userId, searchTitle);
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
        getMemberIndexDto().setListContentDto(listContentDto);

    }

}
