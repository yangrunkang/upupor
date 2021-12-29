package com.upupor.service.business.manage.business;

import com.upupor.service.business.aggregation.service.MemberIntegralService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.dto.page.common.ListIntegralDto;
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
