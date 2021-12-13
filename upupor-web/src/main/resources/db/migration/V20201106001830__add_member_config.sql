CREATE TABLE `member_config` (
 `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
 `config_id` varchar(32) NOT NULL COMMENT '配置id',
 `user_id` varchar(32) NOT NULL COMMENT '用户id',
 `bg_img` varchar(2048) DEFAULT NULL COMMENT '用户背景样式配置',
 `interval_time_create_content` bigint(11) DEFAULT NULL COMMENT '添加文章时间间隔(单位:秒)',
 `create_time` bigint(11) NOT NULL COMMENT '创建时间',
 `sys_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户配置';