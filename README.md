saf完全基于springboot构建，所有组件的集成全部基于注解，各类组件的配置全部基于配置中心，本地做到0配置文件。

|              版本               |                            概述                              |                       缺点/问题                       |
| ------------------------------------ | ------------------------------------------------------------------- | --------------------------------------------------- |
| saf-1.0.0                      |      第一版                               | 工程结构不合理，saf-boot-starter,saf-project,saf-samples应该拆分成3个工程。 saf-boot-starter给业务开发者使用；saf-project是架构师使用；saf-samples是使用范例。拆分后才能达到架构师和业务的完全解耦，互不影响。prometheus集成有些bug。|
| saf-1.0.6                      |      拆分成两组/四个工程。架构师范围：saf, saf-sample；业务使用者范围：saf-boot-starter, saf-boot-starter-samples。                               | 从工程拓扑上完全将架构与业务解耦。 |

# (1).文档体系

[saf-doc](https://github.com/saf-group/saf-doc)

# (2).姊妹项目

提供可以供实验的k8s配置：

https://github.com/hepyu/k8s-app-config

生产级镜像：

https://github.com/hepyu/oraclejdk-docker-image

https://github.com/hepyu/rocketmq-docker-image

# (3).saf含义与定位

## 1.含义一

simple application framework

saf定位是一个简单的框架，基于业界成熟的各种方案，封装为注解的方式给业务方使用；

saf不会自己造轮子，全部采用业界成熟/先进的开源产品，并且都经过生产环境的严格检验，比如配置中心采用携程的apollo，rpc采用微博的motan，监控采用容器化时代的prometheus，jvm缓存采用guava，等等。

## 2.含义二

simple application future

对于业务人员一个简单便捷的开发未来，让业务方全部精力集中在业务上。

## 3.定位

kubernetes容器化时代下，移动互联网生产级别的轻量级高效后端框架。


# (4).包结构设计

完全根据springboot的包结构思想进行设计，将框架实现与业务使用完全解耦，架构师可以在不影响业务的前提之下对框架进行任意进化（前提是保证saf-boot-starter的严谨与正确性）。

![image](https://github.com/saf-group/saf-doc/blob/master/images/saf%E6%A1%86%E6%9E%B6%E5%B7%A5%E7%A8%8B%E6%8B%93%E6%89%91%E8%A7%A3%E6%9E%90.jpg)

## 1.saf

这个工程只有架构师可以修改。

严格禁止业务开发者在pom中直接引入这个工程下的包依赖。

核心工程，包含基础组件，中间件等核心模块的集成，并且定义全局包依赖。

注意：

elasticsearch这类组件和特殊，因为es的不同版本的api差异极大，生产环境中是有可能存在多个版本的es-server集群的，也需要支持这类极端情况。

## 2.saf-sample

这个工程只有架构师可以修改。

主要用于对saf框架进行测试，如调试依赖包，集成测试等。

基于注解方式实现生产环境中几乎所有的组件，业务方只需要使用注解并且在配置中心配置后即可使用。

## 3.saf-boot-starter

这个工程只有架构师可以修改。

这个工程是供业务开发者选择合适的starter进行业务开发，提供了满足各类业务app的pom定义，业务方根据需要按需索取适合的定义。

同时也提供了经典的pom定义，如saf-boot-starter-web，saf-boot-starter-rocketmq等，比如如果你只是写个简单的rocketmq消费者，可以直接引入saf-boot-starter-rocketmq和saf-boot-starter-configcenter-apollo即可，然后只需要一行注解即可实例化rocketmq的bean，要注意mq的相关配置在apollo后台配好。

## 4.saf-boot-starter-sample

这个工程只有架构师可以修改。

主要用于对saf-boot-starter进行测试，如调试依赖包，集成测试等,保证提供给业务方的saf-boot-starter是稳定可靠的版本。

基于注解方式实现生产环境中几乎所有的组件，业务方只需要使用注解并且在配置中心配置后即可使用。

# (5).如何体验

[saf-2：部署saf-sample-allinone]()

# (6).TODO

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
  
## 微信公众号：千里行走

## 头条技术号：实战架构

## 实战交流群

![image](https://github.com/hepyu/saf/blob/master/images/k8s.png)
