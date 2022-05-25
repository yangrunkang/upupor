### 移除镜像
```
docker rmi 镜像名
```

### 迁移oss文件,变更db数据
```
update member set via = REPLACE(via, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor');
update file set file_url = REPLACE(file_url, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor');
update apply_document set upload = REPLACE(upload, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor');
update business_config set `value` = REPLACE(`value`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor') WHERE type = 1; 
update `comment` set `md_comment_content` = REPLACE(`md_comment_content`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor'); 
update `comment` set `comment_content` = REPLACE(`comment_content`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor'); 
update `content_extend` set `detail_content` = REPLACE(`detail_content`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor'); 
update `content_extend` set `markdown_content` = REPLACE(`markdown_content`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor'); 
update `radio` set `radio_url` = REPLACE(`radio_url`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor'); 
update `tag` set `icon` = REPLACE(`icon`, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor'); 
update member_extend set bg_img = REPLACE(bg_img, 'https://upupor-img.oss-cn-hangzhou.aliyuncs.com', 'https://www.upupor.com/minio_upupor');
```
