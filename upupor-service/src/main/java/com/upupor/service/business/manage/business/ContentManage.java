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

import com.upupor.framework.CcConstant;
import com.upupor.service.data.aggregation.service.ContentService;
import com.upupor.service.business.manage.AbstractManage;
import com.upupor.service.business.manage.CommonService;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.business.manage.service.ContentManageService;
import com.upupor.framework.BusinessException;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.ListContentReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.framework.ErrorCode.CONTENT_NOT_EXISTS;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class ContentManage extends AbstractManage {
    @Resource
    private ContentManageService contentManageService;
    @Resource
    private CommonService commonService;
    @Resource
    private ContentService contentService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();
        String searchTitle = manageDto.getSearchTitle();
        String select = manageDto.getSelect();

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setUserId(userId);
        listContentReq.setPageSize(pageSize);
        listContentReq.setPageNum(pageNum);
        listContentReq.setSearchTitle(searchTitle);
        // 筛选
        listContentReq.setSelect(select);

        ListContentDto listContentDto = null;
        try {
            listContentDto = contentManageService.listContent(listContentReq);
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
        contentService.handlePinnedContent(listContentDto, userId);
        getMemberIndexDto().setListContentDto(listContentDto);
    }

    @Override
    public String viewName() {
        return CcConstant.UserManageView.USER_MANAGE_CONTENT;
    }

    @Override
    public String viewDesc() {
        return "内容管理";
    }
}