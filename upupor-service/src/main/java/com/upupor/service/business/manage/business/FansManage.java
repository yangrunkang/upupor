package com.upupor.service.business.manage.business;

import com.upupor.service.business.aggregation.service.FanService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.dto.page.common.ListFansDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class FansManage extends AbstractManageInfoGet {
    @Resource
    private FanService fanService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        String userId = manageDto.getUserId();
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        ListFansDto listFansDto = fanService.getFans(userId, pageNum, pageSize);
        getMemberIndexDto().setListFansDto(listFansDto);
    }

}
