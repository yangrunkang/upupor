/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.service.business.editor;

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dto.OperateContentDto;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.PinnedStatus;
import com.upupor.framework.BusinessException;
import com.upupor.service.utils.SessionUtils;
import com.upupor.service.outer.req.content.UpdateStatusReq;
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
public class UpdateStatus extends AbstractEditor<UpdateStatusReq> {
    @Override
    protected EditorType editorType() {
        return EditorType.UPDATE_STATUS;
    }

    private ContentEnhance editContentEnhance;

    @Override
    protected void check() {
        UpdateStatusReq updateStatusReq = getReq();
        // check
        String contentId = updateStatusReq.getContentId();
        editContentEnhance = contentService.getManageContentDetail(contentId);
        Content editContent = editContentEnhance.getContent();
        // 校验内容所属的用户id是否是当前用户
        SessionUtils.checkOperatePermission(editContent.getUserId());

        if (Objects.nonNull(editContent.getPinnedStatus()) && PinnedStatus.PINNED.equals(editContent.getPinnedStatus()) && !ContentStatus.NORMAL.equals(updateStatusReq.getStatus())) {
            throw new BusinessException(FIRST_CANCEL_PINNED);
        }
    }

    @Override
    protected OperateContentDto doBusiness() {
        UpdateStatusReq updateStatusReq = getReq();
        Content editContent = editContentEnhance.getContent();
        editContent.setStatus(updateStatusReq.getStatus());
        editContent.setSysUpdateTime(new Date());
        boolean result = contentMapper.updateById(editContent) > 0;


        return OperateContentDto.builder()
                .contentId(editContent.getContentId())
                .success(result)
                .status(editContent.getStatus())
                .build();
    }

}
