# Upupor 
让每个人享受分享
<!--
![项目访问次数](https://visitor-badge.glitch.me/badge?page_id=github_yangrunkang_upupor)
<a href="https://www.producthunt.com/posts/upupor?utm_source=badge-featured&utm_medium=badge&utm_souce=badge-upupor" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/featured.svg?post_id=327650&theme=dark" alt="Upupor - UGC Website,Open source | Product Hunt" style="width: 250px; height: 54px;" width="250" height="54" /></a>
-->

## 分支
* **thymeleaf-version**   upupor服务端渲染分支(**upupor.com主分支**)
* **new-api**             upupor-api服务分支

## 网站
https://www.upupor.com

## Star数
[![Stargazers over time](https://starchart.cc/yangrunkang/upupor.svg)](https://starchart.cc/yangrunkang/upupor)

<!--
## 统计
![Alt](https://repobeats.axiom.co/api/embed/18e821e0b79bcf13c83f8d21d669f7264abace1a.svg "Repobeats analytics image")
-->

## Upupor编码
出于自己对技术的纯粹和要求,在upupor项目的开发过程中,会遵循以下准则:
1. 充分使用Java基本特性: 封装、继承、多态
2. 对类似的业务逻辑进行抽象,提取出公用的逻辑,朝着易扩展、易维护的方向前进
3. 定期会Code Review,因为自己的技术能力和思维能力在不断地提升,所以会定期的Review代码,会将自己的知识进行沉淀和落地
4. 减少外部依赖。除了最基本的依赖之外,会尽可能减少外部依赖的引入。例如,upupor的全局检索,是基于lucene实现的,而不是引入ES来实现
5. 算法的引入,会将学到的算法应用其中。例如,雪花算法,upupor的资源id生成有雪花算法的参与



## 部署文档
- [Docker部署](docs/deploy/docker部署文档.md)
- [常规部署文档](docs/deploy/常规部署文档.md)
- [开发环境配置文档](docs/deploy/开发环境配置文档.md)

## 运维文档
- [应用运维](docs/run.md)

## markdown编辑器
> markdown编辑器fork了腾讯的cherry-markdown进行了微改动以适配upupor网站业务及风格

地址:
https://github.com/upupor/cherry-markdown

## 联系方式
- Email: yangrunkang53@gmail.com
- wechat: Bla277225635

<img src="docs/wechat.jpg" width="250px" height="250px" alt="微信">

## QA
### 0. 数据库建表语句在哪里?
upupor使用了flyway,因此数据库SQL文件维护在upupor-web/src/main/resources/db/migration目录,只需要配置好DB然后启动程序,就会创建相应的表以及执行相应的SQL.
后续如果想新增表或者执行SQL可以直接在migration目录按照规则新建migration文件即可,程序启动时会自动执行.

### 1.为什么采用 thymeleaf 建站?
upupor用 thymeleaf 的原因是为了做SEO,目前所有页面均采用服务端渲染。在做upupor网站的初期(2019年)前端框架SSR还不成熟,所以没有采用前端SSR.
现在前端SSR方案成熟了很多,现在如果做SEO的话,可以采用前端SSR.

### 2.计划使用前端SSR方案重构upupor吗?
目前没有计划,我们永远不要为了"技术而技术"! 一个项目是否够好要看是否满足需求。
当然成本也在考虑的范围内,有多少资源做多少事情。一个人开发的时候才用最小技术栈永远是最优的选择。所以从这一点来说,upupor适合单人作战.
> 用最低的成本拿到相等的收益

### 3.Upupor接入了哪些三方服务?
1. Google分析 https://marketingplatform.google.com/about/analytics/?hl=zh-CN
2. Google广告 https://www.google.com/intl/zh-CN_cn/adsense/start/

### 4.程序启动时图片不显示怎么处理?
因为有防盗链,只允许localhost、*.upupor.com两个域访问。解决方法:
- 本机环境使用[http://localhost:2020](http://localhost:2020)访问
- 将系统默认的图片地址修改为您本地的图片地址或者您可访问的远程图片地址

### 5.upupor页面加载性能如何?
下面是Google PageSpeed Insights桌面版的测试结果
![PageSpeed Insights性能测试](docs/insight.png)

### Google Search 页面体验报告
<img width="923" alt="image" src="https://user-images.githubusercontent.com/46257933/169191026-e454c17d-00ac-404d-a12d-d5a150a7182a.png">




<!--
## Upupor开发进度
[开发进度面板](https://github.com/users/yangrunkang/projects/1)
-->
