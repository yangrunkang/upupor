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

package com.upupor.data.dao.entity;

import com.upupor.data.dao.BaseEntity;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.OriginType;
import com.upupor.data.types.PinnedStatus;
import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

/**
 * 内容
 *
 * @author: runkangyang (cruise)
 * @date: 2020.01.05 23:37
 */
@Data
public class Content extends BaseEntity {

    private String contentId;

    private String userId;

    private String title;

    private String shortContent;

    private String picture;

    /**
     * 文章类型 0-默认 1-技术 2-问答 3-分享 4-职场 5-记录
     */
    private ContentType contentType;

    /**
     * 文章标签,多个以","隔开
     */
    private String tagIds;

    /**
     * 状态 0-正常 1-草稿[已移除] 2-审核中 3-异常 4-删除 5-回收站 6-仅自己可见
     */
    private ContentStatus status;


    /**
     * 置顶状态
     */
    private PinnedStatus pinnedStatus;

    /**
     * 编辑次数
     */
    private Integer editTimes;
    /**
     * 编辑时间
     */
    private Long editTime;


    private OriginType originType;
    private String noneOriginLink;

    /**
     * 声明Id
     */
    private Integer statementId;

    private Long createTime;
    /**
     * 最新回复的时间
     */
    private Long latestCommentTime;
    /**
     * 最新回复的人
     */
    private String latestCommentUserId;


    /**
     * 关键字
     */
    private String keywords;

    private Content() {
    }


    public static Content init() {
        Content newContent = new Content();
        newContent.setTagIds(CcConstant.EMPTY_STR);
        newContent.setStatus(ContentStatus.NORMAL);
        newContent.setEditTimes(0);
        newContent.setPinnedStatus(PinnedStatus.UN_PINNED);
        newContent.setOriginType(OriginType.ORIGIN);
        newContent.setCreateTime(CcDateUtil.getCurrentTime());
        newContent.setLatestCommentTime(CcDateUtil.getCurrentTime());
        return newContent;
    }

}
