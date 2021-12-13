CREATE TABLE  IF NOT EXISTS `radio` (
    `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `radio_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '音频id',
    `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户Id',
    `content_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关联文章Id',
    `radio_intro` text COLLATE utf8mb4_bin COMMENT '音频简介',
    `radio_url` text COLLATE utf8mb4_bin COMMENT '音频链接',
    `status` int(2) DEFAULT NULL COMMENT '状态 0-公开 1-隐藏 2-删除',
    `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
    `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='电台音频表';