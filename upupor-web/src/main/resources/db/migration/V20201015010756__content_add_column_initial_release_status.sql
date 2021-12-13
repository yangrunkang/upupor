alter table content add column `is_initial_status` int(2) DEFAULT 0 COMMENT '是否是第一次发布 0-未发布 1-已发布';
update content set is_initial_status = 1 where status = 0;