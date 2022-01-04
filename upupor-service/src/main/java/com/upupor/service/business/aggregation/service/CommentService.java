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

package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.spi.req.AddCommentReq;
import com.upupor.service.spi.req.ListCommentReq;

import java.util.List;

/**
 * 评论服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:57
 */
public interface CommentService {
    /**
     * 添加评论
     *
     * @param addCommentReq
     * @return
     */
    Comment toComment(AddCommentReq addCommentReq);

    /**
     * 更新评论
     *
     * @param comment
     * @return
     */
    Boolean update(Comment comment);


    /**
     * 获取评论列表
     *
     * @param listCommentReq
     * @return
     */
    ListCommentDto listComment(ListCommentReq listCommentReq);

    /**
     * 根据评论id获取评论
     *
     * @param commentId
     * @return
     */
    Comment getCommentByCommentId(String commentId);

    /**
     * 评论总数
     *
     * @return
     */
    Integer total();

    /**
     * 绑定评论的用户名
     *
     * @param commentList
     */
    void bindCommentUser(List<Comment> commentList);

    void bindContentComment(Content content, Integer pageNum, Integer pageSize);

    void bindRadioComment(Radio radio, Integer pageNum, Integer pageSize);

    /**
     * 根据目标id统计评论数
     *
     * @param targetId
     */
    Integer countByTargetId(String targetId);

}