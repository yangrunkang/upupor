/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.business.manage.business;

import com.alibaba.fastjson2.JSON;
import com.upupor.framework.CcConstant;
import com.upupor.service.business.manage.AbstractManage;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.business.manage.service.ApplyManageService;
import com.upupor.service.dto.page.apply.ApplyContentDto;
import com.upupor.service.dto.page.common.ListApplyDto;
import com.upupor.service.outer.req.AddTagReq;
import com.upupor.service.types.ApplySource;
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
public class ApplyManage extends AbstractManage {

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
            if (apply.getApplySource().equals(ApplySource.AD)) {
                ApplyContentDto applyContentDto = JSON.parseObject(apply.getApplyContent(), ApplyContentDto.class);
                apply.setApplyContent(applyContentDto.getApplyIntro());
            }
            if (apply.getApplySource().equals(ApplySource.TAG)) {
                AddTagReq addTagReq = JSON.parseObject(apply.getApplyContentDto().getApplyIntro(), AddTagReq.class);
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
            if (apply.getApplySource().equals(ApplySource.CONSULTING_SERVICE)) {
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

    @Override
    public String viewName() {
        return CcConstant.UserManageView.USER_MANAGE_APPLY;
    }

    @Override
    public String viewDesc() {
        return "申请管理";
    }
}