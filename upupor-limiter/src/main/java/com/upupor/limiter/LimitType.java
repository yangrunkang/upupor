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

package com.upupor.limiter;

import lombok.Getter;

/**
 * 限制类型
 * @note: 一期: 主要是对付费资源进行限制
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-18 02:38
 * @email: yangrunkang53@gmail.com
 */
@Getter
public enum LimitType {
    /**
     * 创建文章
     */
    CREATE_CONTENT,
    /**
     * 创建电台
     */
    CREATE_RADIO,
    /**
     * 留言
     */
    CREATE_MESSAGE_ON_BOARD,
    /**
     * 添加代办
     */
    CREATE_TODO,
    /**
     * 评论
     */
    CREATE_COMMENT,

    /**
     * 发送优先验证码
     */
    SEND_EMAIL_VERIFY_CODE,

    /**
     * 反馈
     */
    FEED_BACK,

    /**
     * 上次文章图片
     */
    UPLOAD_CONTENT_IMAGE,

    /**
     * 上传头像
     */
    UPLOAD_PROFILE_IMAGE,

    /**
     * 上传电台
     */
    UPLOAD_RADIO_FILE,

    /**
     * 全局检索
     */
    GLOBAL_SEARCH,

    /**
     * 点赞
     */
    CLICK_LIKE,

    /**
     * 关注
     */
    CLICK_ATTENTION,

    /**
     * 收藏
     */
    CLICK_COLLECT,

}
