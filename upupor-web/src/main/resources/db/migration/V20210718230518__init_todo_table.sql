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

CREATE TABLE `todo` (
    `id` bigint(11) NOT NULL AUTO_INCREMENT,
    `user_id` varchar(32) NOT NULL COMMENT '用户Id',
    `todo_id` varchar(32) NOT NULL COMMENT '代办Id',
    `title` varchar(512) NOT NULL COMMENT '代办标题',
    `start_time` bigint(11) DEFAULT NULL,
    `end_time` bigint(11) DEFAULT NULL COMMENT '开始时间',
    `status` int(2) NOT NULL COMMENT '状态 0-未完成 1-已完成 2-已暂停 3-删除',
    `create_time` bigint(11) NOT NULL COMMENT '创建时间',
    `sys_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '代办表';


CREATE TABLE `todo_detail` (
   `id` bigint(11) NOT NULL AUTO_INCREMENT,
   `todo_id` varchar(32) NOT NULL COMMENT '代办Id',
   `detail` text COMMENT '代办明细',
   `create_time` bigint(11) NOT NULL COMMENT '创建时间',
   `sys_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '代办明细表';
