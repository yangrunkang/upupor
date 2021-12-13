alter table content add column `latest_comment_time` bigint(11) DEFAULT NULL COMMENT '最新评论时间';
alter table content add column `latest_comment_user_id` varchar(32) DEFAULT NULL COMMENT '最新评论的用户Id';