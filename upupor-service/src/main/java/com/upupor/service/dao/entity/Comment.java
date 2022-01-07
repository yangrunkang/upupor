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

package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.types.CommentAgree;
import com.upupor.service.types.CommentStatus;
import com.upupor.service.types.ContentType;
import joptsimple.internal.Strings;
import lombok.Data;

import java.util.Objects;

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

    /*******************/

    /**
     * 被评论的文章标题
     */
    @TableField(exist = false)
    private Content content;

    /**
     * 评论者自己
     */
    @TableField(exist = false)
    private Member member;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createDate;
    @TableField(exist = false)
    private String createDateDiff;

    @TableField(exist = false)
    private String floorNum;


    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        if(Objects.isNull(createTime)){
            return Strings.EMPTY;
        }
        return CcDateUtil.snsFormat(createTime);
    }



}
