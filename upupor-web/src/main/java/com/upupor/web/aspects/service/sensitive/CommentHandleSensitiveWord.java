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

package com.upupor.web.aspects.service.sensitive;

import com.upupor.data.dao.entity.Comment;
import com.upupor.security.sensitive.AbstractHandleSensitiveWord;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-29 20:23
 * @email: yangrunkang53@gmail.com
 */
public class CommentHandleSensitiveWord extends AbstractHandleSensitiveWord<Comment> {

    @Override
    public Boolean isHandle(Class<?> clazz) {
        return Comment.class.getName().equals(clazz.getName());
    }

    @Override
    protected void handle(Comment comment) {
        comment.unZip();
        comment.setCommentContent(replaceSensitiveWord(comment.getCommentContent()));
        comment.setMdCommentContent(replaceSensitiveWord(comment.getMdCommentContent()));
        comment.zip();
    }
}
