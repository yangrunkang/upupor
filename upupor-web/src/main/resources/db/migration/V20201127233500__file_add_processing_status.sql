alter table `file` add column `upload_status` int(2) DEFAULT NULL COMMENT '状态 0-上传中 1-上传完毕';

update `file` set upload_status = 1;