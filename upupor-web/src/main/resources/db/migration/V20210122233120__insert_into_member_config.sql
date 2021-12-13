
insert into member_config (config_id,user_id,create_time,sys_update_time)
select substr(md5(rand()),1,20),user_id,UNIX_TIMESTAMP(),NOW() from member where user_id not in (select DISTINCT user_id from member_config);
