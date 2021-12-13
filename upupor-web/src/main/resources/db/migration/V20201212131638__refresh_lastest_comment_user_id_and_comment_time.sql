
set @@sql_mode
    ='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';

update content ct
    left join
    (
        select target_id,user_id,max(create_time) create_time from `comment` GROUP BY target_id
    )  tmp on tmp.target_id = ct.content_id
set
    ct.latest_comment_time = IFNULL(tmp.create_time,ct.create_time),
    ct.latest_comment_user_id = tmp.user_id
where ct.latest_comment_time is null and ct.latest_comment_user_id is null;