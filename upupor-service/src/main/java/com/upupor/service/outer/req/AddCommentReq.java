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

package com.upupor.service.outer.req;

import com.upupor.data.types.ContentType;
import lombok.Data;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:38
 */
@Data
public class AddCommentReq {
    /**
     * 目标id
     */
    private String targetId;
    /**
     * 评论来源
     */
    private ContentType commentSource;
    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * markdown评论内容
     */
    private String mdCommentContent;

    /**
     * 记录回复某一个评论的用户Id(回复给用户)
     */
    private String replyToUserId;

    /**
     * 被回复的楼层
     */
    private Long beFloorNum;
}
