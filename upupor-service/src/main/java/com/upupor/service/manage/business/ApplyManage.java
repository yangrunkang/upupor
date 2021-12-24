package com.upupor.service.manage.business;

import com.alibaba.fastjson.JSON;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.apply.ApplyContentDto;
import com.upupor.service.dto.page.common.ListApplyDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.manage.service.ApplyManageService;
import com.upupor.spi.req.AddTagReq;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class ApplyManage extends AbstractManageInfoGet {

    @Resource
    private ApplyManageService applyManageService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {

        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();
        ListApplyDto listApplyDto = applyManageService.listApplyListByUserIdManage(userId, pageNum, pageSize);

        // 处理申请内容
        handleApplyContent(listApplyDto);

        getMemberIndexDto().setListApplyDto(listApplyDto);

    }

    private void handleApplyContent(ListApplyDto listApplyDto) {
        if (Objects.isNull(listApplyDto)) {
            return;
        }
        if (CollectionUtils.isEmpty(listApplyDto.getApplyList())) {
            return;
        }

        listApplyDto.getApplyList().forEach(apply -> {
            if (apply.getApplySource().equals(CcEnum.ApplySource.AD.getSource())) {
                ApplyContentDto applyContentDto = JSON.parseObject(apply.getApplyContent(), ApplyContentDto.class);
                apply.setApplyContent(applyContentDto.getApplyIntro());
            }
            if (apply.getApplySource().equals(CcEnum.ApplySource.TAG.getSource())) {
                AddTagReq addTagReq = JSON.parseObject(apply.getApplyContent(), AddTagReq.class);
                StringBuilder str = new StringBuilder();
                str.append("<strong>页面</strong>: ").append(addTagReq.getPageName()).append(CcConstant.HTML_BREAK_LINE);
                if (!StringUtils.isEmpty(addTagReq.getTagName())) {
                    str.append("<strong>标签名</strong>: ").append(addTagReq.getTagName());
                }
                if (!StringUtils.isEmpty(addTagReq.getChildTagName())) {
                    str.append(CcConstant.HTML_BREAK_LINE).append("<strong>子标签名</strong>: ").append(addTagReq.getChildTagName());
                }
                apply.setApplyContent(str.toString());
            }
            if (apply.getApplySource().equals(CcEnum.ApplySource.CONSULTING_SERVICE.getSource())) {
                ApplyContentDto applyContentDto = JSON.parseObject(apply.getApplyContent(), ApplyContentDto.class);
                StringBuilder str = new StringBuilder();
                str.append("<strong>主题</strong>: ").append(applyContentDto.getApplyProject()).append(CcConstant.HTML_BREAK_LINE);
                if (!StringUtils.isEmpty(applyContentDto.getApplyIntro())) {
                    str.append("<strong>描述</strong>: ").append(applyContentDto.getApplyIntro());
                }
                apply.setApplyContent(str.toString());
            }
        });

    }
}