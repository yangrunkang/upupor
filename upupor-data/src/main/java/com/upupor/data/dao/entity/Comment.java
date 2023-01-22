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

package com.upupor.data.dao.entity;

import com.upupor.data.dao.BaseEntity;
import com.upupor.data.types.CommentAgree;
import com.upupor.data.types.CommentStatus;
import com.upupor.data.types.ContentType;
import lombok.Data;

@Data
public class Comment extends BaseEntity {

    private String userId;

    private String commentId;

    private String targetId;

    private ContentType commentSource;

    private CommentStatus status;

    private CommentAgree agree;

    private Integer likeNum;

    private Long createTime;

    private String commentContent;

    private String mdCommentContent;

    /**
     * 被评论的楼层
     */
    private Integer beFloorNum;

    /**
     * 记录回复某一个评论的用户Id
     */
    private String replyToUserId;


    public static Comment empty() {
        return new Comment();
    }
}
