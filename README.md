# 微信公众号：千里行走

# 头条技术号：实战架构

# 实战交流群

![image](https://github.com/hepyu/saf/blob/master/images/k8s.png)

# 姊妹项目

提供可以供实验的k8s配置：

https://github.com/hepyu/k8s-app-config

生产级镜像：

https://github.com/hepyu/oraclejdk-docker-image

https://github.com/hepyu/rocketmq-docker-image

# (1).saf含义与定位

## 1.1.含义一

simple application framework

saf定位是一个简单的框架，基于业界成熟的各种方案，封装为注解的方式给业务方使用；

saf不会自己造轮子，全部采用业界成熟/先进的开源产品，并且都经过生产环境的严格检验，比如配置中心采用携程的apollo，rpc采用微博的motan，监控采用容器化时代的prometheus，jvm缓存采用guava，等等。

## 1.2.含义二

simple application future

对于业务人员一个简单便捷的开发未来，让业务方全部精力集中在业务上。

## 1.3.定位

kubernetes容器化时代下，移动互联网生产级别的轻量级高效后端框架。


# (2).包结构设计

完全根据springboot的包结构思想进行设计，采用父子双parent-pom方式组织包结构，见下图：

![image](https://github.com/hepyu/saf-private/blob/master/images/saf-framework/%E5%8C%85%E7%BB%93%E6%9E%84%E8%AE%BE%E8%AE%A1.jpg)

## 2.1.saf-parent

是顶级工程，定义saf所用到的所有全局依赖，原则上saf不允许在框架下的子包使用非全局定义的jar包。

saf-parent仅仅供saf框架下的所有包使用，业务服务不能使用，只能继承saf-boot-parent，这样就将框架和业务隔离，也是接口与实现更高层面的体现。

例外：

elasticsearch这类例外，因为es的不同版本的api差异极大，生产环境中是有可能存在多个版本的es-server集群的，也需要支持这类极端情况。

## 2.2.saf-boot-parent

类似spring-boot-start，供业务使用，其子工程提供了满足各类业务app的pom定义。

## 2.3.saf-boot-starters

供业务使用，其子工程提供了满足各类业务app的pom定义，业务方根据需要按需索取适合的定义；

同时也提供了经典的pom定义，如saf-boot-starter-web，saf-boot-starter-rocketmq等;

## 2.4.saf-projects

基于注解方式实现生产环境中几乎所有的组件，业务方只需要使用注解并且在配置中心配置后即可使用。

# (3).如何体验

## 3.1. saf提供了allinonedemo，rpc与web的各自demo

rpc-allinone-demo:https://github.com/hepyu/saf/tree/master/saf-samples/saf-sample-allinone/saf-sample-allinone-service

web-allinone-demo:https://github.com/hepyu/saf/tree/master/saf-samples/saf-sample-allinone/saf-sample-allinone-web

## 3.2.demo运行前提

需要部署redis-cluster, rocketmq, mysql, apollo，zookeeper；然后根据demo下的readme方式启动。

对于上述组件，笔者提供了容器化方式，位于：https://github.com/hepyu/k8s-app-config/tree/master/yaml/min-cluster-allinone


# (4).TODO

目前的master code需要继续打磨，暂时不到发布release的阶段；

由于笔者完全重构了我们生产级的code，特别是工程设计，所以目前的master有可能存在bug，需要完善全方位的测试用例，继续打磨；

目前master的code程度，跑提供的allinone demo完全没有问题；

1.打磨saf code，完成第一版release。

2.使用trello规范TODO list。

3.整理全方位的saf文档。

4.提供容器化级别的监控(saf代码已经支持)。

5.提供/完善全方位的测试用例。

6.等等。


# (*).如何发布

- 准备发布

  ```bash
  mvn release:prepare -Darguments="-DskipTests"
  ```

- 正式发布

  ```bash
  mvn release:perform -DuseReleaseProfile=false
  ```
