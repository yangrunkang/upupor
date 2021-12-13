CREATE TABLE IF NOT EXISTS `slogan_path` (
       `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `path` varchar(128) DEFAULT NULL COMMENT '路径',
       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='slogan允许的路径';