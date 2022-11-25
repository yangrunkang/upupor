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

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.data.dto.page.common.ListCommentDto;
import com.upupor.data.types.RadioStatus;
import com.upupor.data.types.UploadStatus;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Radio extends BaseEntity {

    private String radioId;

    private String userId;

    private String contentId;

    private RadioStatus status;

    private Long createTime;


    private String radioIntro;

    /**
     * 最新回复的时间
     */
    private Long latestCommentTime;
    private String radioUrl;

    /**
     * 最新回复的人
     */
    private String latestCommentUserId;
    /****************************************/
    /**
     * 当前电台上传状态
     */
    @TableField(exist = false)
    private UploadStatus uploadStatus;

    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentTotal;

    /**
     * 电台创作者
     */
    @TableField(exist = false)
    private Member member;

    /**
     * 评论内容
     *
     * @return
     */
    @TableField(exist = false)
    private ListCommentDto listCommentDto;

    /**
     * 文章数据
     */
    @TableField(exist = false)
    private ContentData contentData;

    @TableField(exist = false)
    private String createDate;

    @TableField(exist = false)
    private String latestCommentDate;

    @TableField(exist = false)
    private String latestCommentUserName;

    @TableField(exist = false)
    private List<ViewHistory> viewerList;

    public String getLatestCommentDate() {
        if (Objects.isNull(latestCommentTime)) {
            return latestCommentDate;
        }
        return CcDateUtil.timeStamp2Date(latestCommentTime);
    }


    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

}
