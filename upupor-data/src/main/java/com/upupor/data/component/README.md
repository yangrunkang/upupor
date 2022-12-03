## bu,即business,业务的缩写

这里以Model为基础来实现基于DB的业务,与具体的Service层逻辑无关,即这个只提供纯粹的,最小化的,粒度最细的服务

## 服务提供形式

`@Component` 以组件的形式对外提供服务


## 接口类命名方式

业务实体名 + Component

eg:

- 用户组件 MemberComponent


## 接口实现类命名方式

在[接口类命名方式]基础上 + Service

eg:

- 用户组件实现 MemberComponentService


## 接口方法命名方式

business名 + Model后缀

eg:

- 登录模型命名为loginModel()
- 注册模型命名为registerModel()


## 包结构

|-model 模型实体定义

|-service 服务实现

|接口定义
