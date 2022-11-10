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

package com.upupor.service.business.message.model;

import com.upupor.service.types.MessageType;
import lombok.Builder;
import lombok.Data;

/**
 * 消息模型
 *
 * @author Yang Runkang (cruise)
 * @date 2022年11月10日
 * @email: yangrunkang53@gmail.com
 */
@Data
@Builder
public class MessageModel {

    /**
     * 发送给指定用户
     */
    private String toUserId;

    /**
     * 邮件模型
     */
    private EmailModel emailModel;

    /**
     * 站内信模型
     */
    private InnerModel innerModel;


    /**
     * 直接发邮件模型
     */
    private DirectEmailModel directEmailModel;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息ID
     */
    private String messageId;


    /**
     * 站内信Model
     *
     * @author Yang Runkang (cruise)
     * @createTime 2022-11-10 23:37
     * @email: yangrunkang53@gmail.com
     */
    @Data
    @Builder
    public static class InnerModel {

        /**
         * 站内信消息
         */
        private String message;

    }


    /**
     * 邮件Model
     *
     * @author Yang Runkang (cruise)
     * @createTime 2022-11-10 23:37
     * @email: yangrunkang53@gmail.com
     */
    @Data
    @Builder
    public static class EmailModel {
        /**
         * 标题
         */
        private String title;

        /**
         * 内容
         */
        private String content;

    }

    /**
     * 直接发邮件Model
     * 针对无账号用户、忘记密码等操作
     *
     * @author Yang Runkang (cruise)
     * @createTime 2022-11-10 23:47
     * @email: yangrunkang53@gmail.com
     */
    @Data
    @Builder
    public static class DirectEmailModel {
        private String email;
    }
}
