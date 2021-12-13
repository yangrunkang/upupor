CREATE TABLE IF NOT EXISTS `css_pattern` (
 `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
 `css_pattern_id` varchar(32) DEFAULT NULL COMMENT 'cssPattern主键',
 `pattern_content` varchar(512) DEFAULT NULL COMMENT 'cssPattern',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='css_背景模式';