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