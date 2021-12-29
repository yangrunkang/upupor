package com.upupor.service.business.manage.business;

import com.upupor.service.business.aggregation.service.RadioService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class RadioManage extends AbstractManageInfoGet {
    @Resource
    private RadioService radioService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {

        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();
        String searchTitle = manageDto.getSearchTitle();
        ListRadioDto listRadioDto = radioService.listRadioByUserId(pageNum, pageSize, userId, searchTitle);
        getMemberIndexDto().setListRadioDto(listRadioDto);

    }
}