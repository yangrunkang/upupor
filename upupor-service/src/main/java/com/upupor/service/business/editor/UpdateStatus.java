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

package com.upupor.service.business.editor;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.framework.BusinessException;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.outer.req.UpdateContentReq;
import com.upupor.service.types.ContentIsInitialStatus;
import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.PinnedStatus;
import com.upupor.service.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

import static com.upupor.framework.ErrorCode.FIRST_CANCEL_PINNED;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月09日 11:52
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
public class UpdateStatus extends AbstractEditor<UpdateContentReq> {
    @Override
    protected EditorType editorType() {
        return EditorType.UPDATE_STATUS;
    }

    private Content editContent;

    @Override
    protected void check() {
        UpdateContentReq updateContentReq = getReq();
        // check
        String contentId = updateContentReq.getContentId();
        editContent = contentService.getManageContentDetail(contentId);
        // 校验内容所属的用户id是否是当前用户
        ServletUtils.checkOperatePermission(editContent.getUserId());

        if (Objects.nonNull(editContent.getPinnedStatus()) && PinnedStatus.PINNED.equals(editContent.getPinnedStatus()) && !ContentStatus.NORMAL.equals(updateContentReq.getStatus())) {
            throw new BusinessException(FIRST_CANCEL_PINNED);
        }
    }

    @Override
    protected OperateContentDto doBusiness() {
        UpdateContentReq updateContentReq = getReq();
        // 如果只是单纯的在控制中心更改状态
        boolean isSendCreateContentMessage = false;
        if (Objects.nonNull(updateContentReq.getStatus()) && updateContentReq.getStatus() == ContentStatus.NORMAL) {
            if (!ContentIsInitialStatus.FIRST_PUBLISHED.equals(editContent.getIsInitialStatus())) {
                editContent.setCreateTime(CcDateUtil.getCurrentTime());
                editContent.setLatestCommentTime(CcDateUtil.getCurrentTime());
                editContent.setIsInitialStatus(ContentIsInitialStatus.FIRST_PUBLISHED);
                // 第一次将文章正式发出,需要发送邮件
                isSendCreateContentMessage = true;
            }
        }

        editContent.setStatus(updateContentReq.getStatus());
        editContent.setSysUpdateTime(new Date());
        boolean result = contentMapper.updateById(editContent) > 0;

        if (result && isSendCreateContentMessage) {
            // 发布文章事件
            publishContentEvent(editContent);
        }

        return OperateContentDto.builder()
                .contentId(editContent.getContentId())
                .success(result)
                .status(editContent.getStatus())
                .build();
    }

}
