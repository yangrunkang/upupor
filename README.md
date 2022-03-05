# Upupor
让每个人享受分享

<a href="https://www.producthunt.com/posts/upupor?utm_source=badge-featured&utm_medium=badge&utm_souce=badge-upupor" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/featured.svg?post_id=327650&theme=dark" alt="Upupor - UGC Website,Open source | Product Hunt" style="width: 250px; height: 54px;" width="250" height="54" /></a>

## 网站
https://upupor.com

## Upupor开发进度
### [面板](https://github.com/users/yangrunkang/projects/1)

## 部署文档
- [Docker部署](docs/deploy/docker部署文档.md)
- [常规部署文档](docs/deploy/常规部署文档.md)
- [开发环境配置文档](docs/deploy/开发环境配置文档.md)

## markdown编辑器
> markdown编辑器fork了腾讯的cherry-markdown进行了微改动以适配upupor网站业务及风格

地址:
https://github.com/upupor/cherry-markdown

## QA
### 为什么采用 thymeleaf 建站?
upupor用 thymeleaf 的原因是为了做SEO,目前所有页面均采用服务端渲染。在做upupor网站的初期(2019年)前端框架SSR还不成熟,所以没有采用前端SSR.
现在前端SSR方案成熟了很多,现在如果做SEO的话,可以采用前端SSR.

### 计划使用前端SSR方案重构upupor吗?
目前没有计划,我们永远不要为了"技术而技术"! 一个项目是否够好要看是否满足需求。
当然成本也在考虑的范围内,有多少资源做多少事情。一个人开发的时候才用最小技术栈永远是最优的选择。所以从这一点来说,upupor适合单人作战.

### Upupor接入了哪些三方服务?
1. Google分析 https://marketingplatform.google.com/about/analytics/?hl=zh-CN
2. Google广告 https://www.google.com/intl/zh-CN_cn/adsense/start/

### upupor页面加载性能如何?
下面是Google PageSpeed Insights桌面版的测试结果
![PageSpeed Insights性能测试](docs/insight.png)

## 联系方式
- Email: yangrunkang53@gmail.com
- wechat: Bla277225635
> ![扫码](docs/wechat.jpg)
