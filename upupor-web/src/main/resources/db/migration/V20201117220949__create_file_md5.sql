CREATE TABLE  IF NOT EXISTS `file` (
    `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `file_md5` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件md5',
    `file_url` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件url',
    `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
    `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='文件表';