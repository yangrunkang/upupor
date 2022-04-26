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
import com.upupor.service.data.dao.entity.Collect;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.aggregation.service.CollectService;
import com.upupor.service.data.aggregation.service.ContentService;
import com.upupor.service.business.manage.AbstractManage;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.dto.page.common.ListCollectDto;
import com.upupor.service.types.CollectType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class CollectManage extends AbstractManage {
    @Resource
    private CollectService collectService;
    @Resource
    private ContentService contentService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();

        ListCollectDto listCollectDto = collectService.listByUserIdManage(userId, pageNum, pageSize);

        // 封装被收藏的对象
        handleCollectContent(listCollectDto);

        getMemberIndexDto().setListCollectDto(listCollectDto);

    }

    private void handleCollectContent(ListCollectDto listCollectDto) {
        if (Objects.isNull(listCollectDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listCollectDto.getCollectList())) {
            return;
        }

        List<String> contentIdList = listCollectDto.getCollectList().stream()
                .filter(collect -> collect.getCollectType().equals(CollectType.CONTENT))
                .map(Collect::getCollectValue).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentIdList)) {
            return;
        }
        List<Content> contents = contentService.listByContentIdList(contentIdList);
        if (CollectionUtils.isEmpty(contents)) {
            return;
        }
        listCollectDto.getCollectList().forEach(collect -> {
            contents.forEach(content -> {
                if (collect.getCollectValue().equals(content.getContentId())) {
                    collect.setContent(content);
                }
            });
        });
    }

    @Override
    public String viewName() {
        return CcConstant.UserManageView.USER_MANAGE_COLLECTION;
    }

    @Override
    public String viewDesc() {
        return "收藏夹";
    }
}