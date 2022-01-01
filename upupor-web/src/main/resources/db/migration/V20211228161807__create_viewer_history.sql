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

CREATE TABLE IF NOT EXISTS `view_history` (
    `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `target_id` varchar(32) DEFAULT NULL COMMENT '目标id',
    `viewer_user_id` varchar(32) DEFAULT NULL COMMENT '访问者用户id',
    `target_type` int(2) DEFAULT NULL COMMENT '目标类型 0-内容(包含文章和电台)  1-个人主页  2-留言板',
    `delete_status` int(2) DEFAULT NULL COMMENT '删除状态 0-正常 1-删除',
    `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
    `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览记录';