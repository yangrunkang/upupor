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

### minio安装

1. 下载minio对象存储

```
docker pull minio/minio
```

2. 启动minio

```
docker run -p 2187:9000 -p 2188:9001 --name minio -d -e "MINIO_ACCESS_KEY=xljllawe234f" -e "MINIO_SECRET_KEY=aksjgdfkh.Wrs"  minio/minio server --console-address ":9000" --address ":9001" /data

命令解释如下：
* -p：9000是图形界面的端口，9001是API的端口，在使用SDK连接需要用到
* MINIO_ACCESS_KEY：指定图形界面的用户名
* MINIO_SECRET_KEY：指定图形界面的密码
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
LUENCE_INDEX_DIC=全文索引路径
THYMELEAF_CACHE=THYMELEAF缓存开关 true false
LOG_PATH=日志打印地址
UPUPOR_ENV=环境
GOOGLE_DATA_AD_CLIENT_ID=Google广告Id
GOOGLE_AD_RIGHT=右侧广告
GOOGLE_AD_FEED=信息流广告
GOOGLE_TAG_ID=Google分析Id
GOOGLE_GA_4=Google最新GA4媒体资源ID
BUSINESS_STATIC_SOURCE=页面对应的业务存储目录
OSS_SOURCE=OSS资源,目前实现了minio oss,开源的。upupor已经移除了阿里的Oss,原因是太贵了
MINIO_ENDPOINT=minio的服务端点 ,例如 协议://ip:port
MINIO_ACCESS_KEY=minio对象存储的Key
MINIO_SECRET_KEY=minio对象存储的密匙
MINIO_BUCKET_NAME=存储桶对象
MINIO_REQUEST_URL=访问minio资源的前缀,例如:https://www.upupor.com
MINIO_NGINX_ROUTER=nginx转发的url,例如/minio_upupor
SQL_LOG=SQL日志打印实现类配置
```

2. 启动upupor服务

```
docker run -d --name=upupor -p 宿主机端口:upupor服务端口 --env-file ~/.prd_env upupor镜像名
```