## 采用Docker部署

### mysql安装

1. 下载Mysql

```
docker pull mysql
```

2. 启动mysql

```
docker run --name 自定义名称 -p 宿主机端口:mysql服务端口 -e MYSQL_ROOT_PASSWORD=mysql密码自定义 -d mysql
```

### redis安装

1. 下载Redis

```
docker pull redis
```

2. 启动Redis

```
docker run --name 自定义名称 -p 宿主机端口:redis服务端口 -d redis:latest redis-server --appendonly yes --requirepass 'Redis密码'
```

### 编辑upupor配置文件,docker启动upupor时使用
> 配置文件放在 `~` 目录下,文件命名为`.prd_env`

1. 配置upupor服务启动所需变量

```
MYSQL_HOST=mysql数据库host
MYSQL_PORT=mysql数据库端口
MYSQL_USERNAME=mysql数据库用户名
MYSQL_PASSWORD=mysql数据库密码
MYSQL_DATABASE=mysql数据库
REDIS_HOST=redis host
REDIS_PORT=redis端口
REDIS_PASSWORD=redis密码
ALLOW_UPLOAD_PIC_SUFFIX=允许上传图片的后缀
ALLOW_UPLOAD_PIC_QUALITY=允许上传图片的质量
EMAIL_ON=邮件开关(0-关闭 -1开启)
EMAIL_SENDER_NICK_NAME=邮件发送昵称
EMAIL_SENDER_ACCOUNT=邮件发送账户
EMAIL_ACCESS_KEY=访问Key
EMAIL_ACCESS_SECRET=访问秘钥 
WEBSITE=网站
AD_SWITCH=是否开启广告(0-关闭 -1开启)
AD_SWITCH_RIGHT=是否开启右侧广告(0-关闭 -1开启)
ANALYZE_SWITCH=分析开关(0-关闭 -1开启)
OSS_BUCKET_NAME=阿里OSS桶名
OSS_FILE_HOST=阿里OSS文件host
OSS_STATIC=阿里OSS静态文件路径
LUENCE_INDEX_DIC=全文索引路径
THYMELEAF_CACHE=THYMELEAF缓存开关 true false
LOG_PATH=日志打印地址
UPUPOR_ENV=环境
GOOGLE_DATA_AD_CLIENT_ID=Google广告Id
GOOGLE_AD_RIGHT=右侧广告
GOOGLE_AD_FEED=信息流广告
GOOGLE_TAG_ID=Google分析Id
```

2. 启动upupor服务
```
docker run -d --name=upupor -p 宿主机端口:upupor服务端口 --env-file ~/.prd_env upupor镜像名
```