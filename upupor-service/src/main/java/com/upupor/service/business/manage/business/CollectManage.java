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

import com.upupor.data.dao.entity.Collect;
import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.enhance.CollectEnhance;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dto.page.common.ListCollectDto;
import com.upupor.data.types.CollectType;
import com.upupor.framework.CcConstant;
import com.upupor.service.base.CollectService;
import com.upupor.service.base.ContentService;
import com.upupor.service.business.manage.AbstractManage;
import com.upupor.service.business.manage.ManageDto;
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
                .filter(collect -> collect.getCollect().getCollectType().equals(CollectType.CONTENT))
                .map(CollectEnhance::getCollect)
                .map(Collect::getCollectValue)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentIdList)) {
            return;
        }
        List<ContentEnhance> contentEnhanceList = contentService.listByContentIdList(contentIdList);
        if (CollectionUtils.isEmpty(contentEnhanceList)) {
            return;
        }
        listCollectDto.getCollectList().forEach(collect -> {
            contentEnhanceList.forEach(contentEnhance -> {
                Content content = contentEnhance.getContent();
                if (collect.getCollect().getCollectValue().equals(content.getContentId())) {
                    collect.setContent(contentEnhance);
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