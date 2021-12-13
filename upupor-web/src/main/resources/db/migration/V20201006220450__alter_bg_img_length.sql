alter table member_extend modify column bg_img varchar(2048) default  null comment '用户设置的背景图';
alter table css_pattern modify column `pattern_content` varchar(2048) DEFAULT NULL COMMENT 'css模式内容';