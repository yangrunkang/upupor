package com.upupor.service.manage.business;

import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.aggregation.service.MemberIntegralService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class IntegraManage extends AbstractManageInfoGet {
    @Resource
    private MemberIntegralService memberIntegralService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {

        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();

        ListIntegralDto listIntegralDto = memberIntegralService.list(userId, pageNum, pageSize);
        getMemberIndexDto().setListIntegralDto(listIntegralDto);

    }
}
