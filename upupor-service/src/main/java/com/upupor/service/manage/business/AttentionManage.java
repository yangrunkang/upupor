package com.upupor.service.manage.business;

import com.upupor.service.dto.page.common.ListAttentionDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.aggregation.service.AttentionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class AttentionManage extends AbstractManageInfoGet {

    @Resource
    private AttentionService attentionService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        String userId = manageDto.getUserId();
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();

        ListAttentionDto listAttentionDto = attentionService.getAttentions(userId, pageNum, pageSize);
        getMemberIndexDto().setListAttentionDto(listAttentionDto);
    }
}
