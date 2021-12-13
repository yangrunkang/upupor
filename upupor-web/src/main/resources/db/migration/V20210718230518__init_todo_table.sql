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
