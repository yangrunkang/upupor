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

package com.upupor.service.business.content;

import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.utils.ServletUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.service.common.ErrorCode.ARTICLE_NOT_BELONG_TO_YOU;

/**
 * 未公开的内容
 *
 * @author cruise
 * @createTime 2021-12-31 18:03
 */
@Component
public class UnpublishedContent extends AbstractContent {
    @Resource
    private ContentService contentService;

    @Override
    protected Content queryContent() {
        Content content = contentService.getManageContentDetail(getContentId());
        // 校验文章所属人
        String userId = ServletUtils.getUserId();
        if (!content.getUserId().equals(userId)) {
            throw new BusinessException(ARTICLE_NOT_BELONG_TO_YOU);
        }

        return content;
    }

    @Override
    protected void individuateBusiness() {

    }
}
