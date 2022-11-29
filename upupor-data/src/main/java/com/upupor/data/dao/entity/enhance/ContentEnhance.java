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

package com.upupor.data.dao.entity.enhance;

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dto.dao.LastAndNextContentDto;
import com.upupor.data.dto.page.common.ListCommentDto;
import com.upupor.data.dto.page.common.TagDto;
import com.upupor.framework.utils.CcDateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.Time.CONTENT_UPDATE_TIME;
import static com.upupor.framework.CcConstant.Time.NEW_CONTENT_TIME;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-27 03:46
 * @email: yangrunkang53@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class ContentEnhance {
    private Content content;

    private ContentExtendEnhance contentExtendEnhance;

    /**
     * 文章数据
     **/
    private ContentDataEnhance contentDataEnhance;

    private ContentEditReasonEnhance contentEditReasonEnhance;

    /**
     * 用户
     */
    private MemberEnhance memberEnhance;

    private StatementEnhance statementEnhance;


    /**
     * 页面冗余字段 创建日记-年月日
     */
    private String createDate;
    private String latestCommentDate;
    private String createDateDiff;
    private String editDate;

    /**
     * 页面冗余字段
     */
    private String sysUpdateDate;

    /**
     * 页面冗余字段-标签名
     */
    private List<TagDto> tagDtoList;

    /**
     * 评论内容
     *
     * @return
     */
    private ListCommentDto listCommentDto;


    /**
     * 用户
     */
    private String contentTypeDesc;

    /**
     * 收藏数
     */
    private Integer collectTotal;

    /**
     * 评论数
     */
    private Integer commentTotal;

    /**
     * 收藏数
     */
    private Integer collectNum;

    /**
     * 点赞的用户
     */
    private List<MemberEnhance> likesMemberEnhanceList;

    /**
     * 绑定本文的上一篇及下一篇
     */
    private LastAndNextContentDto lastAndNextContentDto;

    /**
     * 文章访问者
     */
    private List<ViewHistoryEnhance> viewerList;

    /**
     * 针对草稿的文章,反查是否已经发布过,做一个标识,提升用户体验的
     */
    private Boolean existedContent;


    public ContentEnhance() {
        init();
    }

    public static Content empty() {
        return new Content();
    }

    private void init() {
        this.collectTotal = 0;
        this.commentTotal = 0;
        this.collectNum = 0;
        this.listCommentDto = new ListCommentDto();
        this.content = Content.init();
    }


    public String getCreateDate() {
        Long createTime = content.getCreateTime();
        if (Objects.isNull(createTime)) {
            return createDate;
        }
        return CcDateUtil.timeStamp2Date(createTime);
    }

    public String getEditDate() {
        Long editTime = content.getEditTime();
        if (Objects.isNull(editTime)) {
            return editDate;
        }
        return CcDateUtil.timeStamp2Date(editTime);
    }

    public String getLatestCommentDate() {
        Long latestCommentTime = content.getLatestCommentTime();
        if (Objects.isNull(latestCommentTime)) {
            return latestCommentDate;
        }
        return CcDateUtil.timeStamp2Date(latestCommentTime);
    }

    /**
     * 最新回复的人姓名
     */
    private String latestCommentUserName;

    public String getCreateDateDiff() {
        return CcDateUtil.timeStamp2DateOnly(content.getCreateTime());
    }

    /**
     * 最近是否更新过
     */
    private Boolean hasLatestEdit = Boolean.FALSE;

    public Boolean getHasLatestEdit() {
        Long editTime = content.getEditTime();
        if (Objects.isNull(editTime)) {
            return hasLatestEdit;
        }
        // 在一天内更新都算最新更新过
        return CcDateUtil.getCurrentTime() - editTime <= CONTENT_UPDATE_TIME;
    }

    /**
     * 是否是最近的新文章
     */
    private Boolean newContent = Boolean.FALSE;

    public Boolean getNewContent() {
        Long createTime = content.getCreateTime();
        if (Objects.isNull(createTime)) {
            return newContent;
        }
        return CcDateUtil.getCurrentTime() - createTime <= NEW_CONTENT_TIME;
    }

    private Boolean hasDraft = Boolean.FALSE;
}
