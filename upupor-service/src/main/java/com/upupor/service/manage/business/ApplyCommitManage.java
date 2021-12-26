package com.upupor.service.manage.business;

import com.upupor.service.dao.entity.Apply;
import com.upupor.service.dto.page.common.ApplyDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.ApplyService;
import com.upupor.service.utils.Asserts;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.service.common.ErrorCode.NOT_EXISTS_APPLY;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class ApplyCommitManage extends AbstractManageInfoGet {
    @Resource
    private ApplyService applyService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {

        Apply apply = applyService.getByApplyId(manageDto.getApplyId());
        Asserts.notNull(apply, NOT_EXISTS_APPLY);

        ApplyDto applyDto = new ApplyDto();
        applyDto.setApply(apply);
        getMemberIndexDto().setApplyDto(applyDto);
    }

}
