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

import com.upupor.data.dao.entity.Comment;
import com.upupor.framework.utils.CcDateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

import static com.upupor.framework.CcConstant.CACHE_COMMENT_TAG_TIME;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-27 03:44
 * @email: yangrunkang53@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEnhance {
    private Comment comment;

    /**
     * 评论者自己
     */
    private MemberEnhance memberEnhance;

    /**
     * 记录回复某一个评论的用户Id
     */
    private MemberEnhance replyMemberEnhance;

    /**
     * 创建时间
     */
    private String createDate;

    private String createDateDiff;

    /**
     * 是否是最近新增的
     */
    private Boolean latestAdded;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(comment.getCreateTime());
    }

    public String getCreateDateDiff() {
        if (Objects.isNull(comment.getCreateTime())) {
            return Strings.EMPTY;
        }
        return CcDateUtil.timeStamp2DateOnly(comment.getCreateTime());
    }

    public Boolean getLatestAdded() {
        return CcDateUtil.getCurrentTime() - comment.getCreateTime() <= CACHE_COMMENT_TAG_TIME;
    }
}
