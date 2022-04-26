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
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.ContentEditReason;
import com.upupor.service.data.dao.mapper.ContentEditReasonMapper;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.outer.req.UpdateContentReq;
import com.upupor.service.types.ContentIsInitialStatus;
import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.OriginType;
import com.upupor.service.types.PinnedStatus;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 更新
 *
 * @author Yang Runkang (cruise)
 * @date 2022年01月09日 11:20
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
public class Edit extends AbstractEditor<UpdateContentReq> {

    @Resource
    private ContentEditReasonMapper contentEditReasonMapper;

    private Content editContent;

    @Override
    protected EditorType editorType() {
        return EditorType.EDIT;
    }

    @Override
    protected void check() {
        UpdateContentReq updateContentReq = getReq();
        editContent = contentService.getManageContentDetail(updateContentReq.getContentId());

        // 校验内容所属的用户id是否是当前用户
        ServletUtils.checkOperatePermission(editContent.getUserId());

        // 检查置顶文章的状态
        pinnedContentCheck(editContent);
    }

    @Override
    protected OperateContentDto doBusiness() {

        // 只在文章状态正常的情况下,记录变更次数
        if (ContentStatus.NORMAL.equals(editContent.getStatus())) {
            updateEditTimes(editContent);
        }

        UpdateContentReq updateContentReq = getReq();

        // 编辑Content
        BeanUtils.copyProperties(updateContentReq, editContent);
        if (!StringUtils.isEmpty(updateContentReq.getTagIds())) {
            editContent.setTagIds(CcUtils.removeLastComma(updateContentReq.getTagIds()));
        }

        // 操作更改为公开、草稿
        boolean isSendCreateContentMessage = false;
        if (Objects.nonNull(updateContentReq.getIsDraftPublic()) && updateContentReq.getIsDraftPublic()) {
            editContent.setStatus(ContentStatus.NORMAL);
            if (!ContentIsInitialStatus.FIRST_PUBLISHED.equals(editContent.getIsInitialStatus())) {
                editContent.setCreateTime(CcDateUtil.getCurrentTime());
                editContent.setLatestCommentTime(CcDateUtil.getCurrentTime());
                editContent.setIsInitialStatus(ContentIsInitialStatus.FIRST_PUBLISHED);
                // 第一次将文章正式发出,需要发送邮件
                isSendCreateContentMessage = true;
            }
        }

        // 从转载变为原创,需要清除转载的链接
        if (OriginType.NONE_ORIGIN.equals(editContent.getOriginType()) && OriginType.ORIGIN.equals(updateContentReq.getOriginType())) {
            editContent.setNoneOriginLink(null);
        }

        editContent.setSysUpdateTime(new Date());
        int totalUpdateCount = contentMapper.updateById(editContent);

        // 内容不等时再变更
        String markdownContent = editContent.getContentExtend().getMarkdownContent();
        if (!updateContentReq.getMdContent().equals(markdownContent)) {
            editContent.getContentExtend().setDetailContent(updateContentReq.getContent());
            editContent.getContentExtend().setMarkdownContent(updateContentReq.getMdContent());
            editContent.getContentExtend().setSysUpdateTime(new Date());
            totalUpdateCount = totalUpdateCount + contentExtendMapper.updateById(editContent.getContentExtend());
        }
        boolean updateSuccess = totalUpdateCount > 0;
        if (updateSuccess && isSendCreateContentMessage) {
            // 发布文章时间
            publishContentEvent(editContent);
        }

        Content reGet = contentService.getContentByContentIdNoStatus(updateContentReq.getContentId());
        return OperateContentDto.builder()
                .contentId(editContent.getContentId())
                .success(updateSuccess)
                .status(reGet.getStatus())
                .build();
    }

    private void pinnedContentCheck(Content editContent) {
        UpdateContentReq updateContentReq = getReq();
        if (Objects.nonNull(editContent.getPinnedStatus()) && editContent.getPinnedStatus().equals(PinnedStatus.PINNED)) {
            if (Objects.nonNull(updateContentReq.getStatus()) && !updateContentReq.getStatus().equals(ContentStatus.NORMAL)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_SET_PINNED_CONTENT_STATUS_NOT_NORMAL);
            }
        }
    }


    private void updateEditTimes(Content editContent) {
        UpdateContentReq updateContentReq = getReq();
        String updateTitleContent = updateContentReq.getTitle() + updateContentReq.getContent();
        String originTitleContent = editContent.getTitle() + editContent.getContentExtend().getDetailContent();
        if (!updateTitleContent.equals(originTitleContent)) {
            Integer editTimes = editContent.getEditTimes();
            if (Objects.isNull(editTimes)) {
                editTimes = 0;
            }
            editTimes = editTimes + 1;
            editContent.setEditTimes(editTimes);
            editContent.setEditTime(CcDateUtil.getCurrentTime());
            // 记录变更原因
            if (!StringUtils.isEmpty(updateContentReq.getEditReason())) {
                ContentEditReason contentEditReason = new ContentEditReason();
                contentEditReason.setContentId(editContent.getContentId());
                contentEditReason.setReason(updateContentReq.getEditReason());
                contentEditReason.setCreateTime(CcDateUtil.getCurrentTime());
                contentEditReason.setSysUpdateTime(new Date());
                contentEditReasonMapper.insert(contentEditReason);
            }
        }
    }


}
