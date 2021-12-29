package com.upupor.service.manage.business;

import com.upupor.service.dto.page.common.ListFansDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.aggregation.service.FanService;
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
