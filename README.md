# Upupor
让每个人享受分享


## 系统变量
> 如果配置有特殊字符,需要加转义字符
```
export MYSQL_HOST=mysql数据库host
export MYSQL_PORT=mysql数据库端口
export MYSQL_USERNAME=mysql数据库用户名
export MYSQL_PASSWORD=mysql数据库密码
export MYSQL_DATABASE=mysql数据库
export REDIS_HOST=redis host
export REDIS_PORT=redis端口
export REDIS_PASSWORD=redis密码
export ALLOW_UPLOAD_PIC_SUFFIX=允许上传图片的后缀
export ALLOW_UPLOAD_PIC_QUALITY=允许上传图片的质量
export EMAIL_ON=邮件开关(0-关闭 -1开启)
export EMAIL_SENDER_NICK_NAME=邮件发送昵称
export EMAIL_SENDER_ACCOUNT=邮件发送账户
export EMAIL_ACCESS_KEY=访问Key
export EMAIL_ACCESS_SECRET=访问秘钥 
export WEBSITE=网站
export AD_SWITCH=是否开启广告(0-关闭 -1开启)
export AD_SWITCH_RIGHT=是否开启右侧广告(0-关闭 -1开启)
export ANALYZE_SWITCH=分析开关(0-关闭 -1开启)
export OSS_BUCKET_NAME=阿里OSS桶名
export OSS_FILE_HOST=阿里OSS文件host
export OSS_STATIC=阿里OSS静态文件路径
export LUENCE_INDEX_DIC=全文索引路径
export THYMELEAF_CACHE=THYMELEAF缓存开关 true false
export LOG_PATH=日志打印地址
export UPUPOR_ENV=环境
export GOOGLE_DATA_AD_CLIENT_ID=Google广告Id
export GOOGLE_AD_RIGHT=右侧广告
export GOOGLE_AD_FEED=信息流广告
```

## 应用内环境变量
> 此为示例,请根据实际情况修改
```
MYSQL_HOST=localhost;MYSQL_PORT=3306;MYSQL_USERNAME=root;MYSQL_PASSWORD=123456;MYSQL_DATABASE=open_base;REDIS_HOST=localhost;REDIS_PORT=6379;REDIS_PASSWORD=;ALLOW_UPLOAD_PIC_SUFFIX=jpg;ALLOW_UPLOAD_PIC_QUALITY=0.5;EMAIL_ON=0;EMAIL_SENDER_NICK_NAME=;EMAIL_SENDER_ACCOUNT=;EMAIL_ACCESS_KEY=;EMAIL_ACCESS_SECRET= ;WEBSITE=http://localhost:2020;AD_SWITCH=0;AD_SWITCH_RIGHT=0;ANALYZE_SWITCH=0;OSS_BUCKET_NAME=;OSS_FILE_HOST=;OSS_STATIC=http://localhost:2020;LUENCE_INDEX_DIC=;THYMELEAF_CACHE=false;LOG_PATH=/Users/yangrunkang/logs;UPUPOR_ENV=dev;GOOGLE_DATA_AD_CLIENT_ID=;GOOGLE_AD_RIGHT=;GOOGLE_AD_FEED=
```