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

package com.upupor.service.business.viewhistory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.dao.mapper.ContentMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-28 18:48
 */
@Component
public class ContentViewHistory extends AbstractViewHistory<Content> {

    @Resource
    private ContentMapper contentMapper;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.CONTENT;
    }

    @Override
    public List<Content> getTargetList() {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .in(Content::getContentId, getViewHistoryTargetIdList());
        return contentMapper.selectList(query);
    }

    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Content content : getTargetList()) {
                if (content.getContentId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(content.getTitle());
                    viewHistory.setUrl("/u/" + content.getContentId());
                    viewHistory.setSource(viewTargetType().getName());
                }
            }
        }
    }

}
