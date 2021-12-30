/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

import com.upupor.service.business.aggregation.service.AttentionService;
import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.dto.page.common.ListAttentionDto;
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
