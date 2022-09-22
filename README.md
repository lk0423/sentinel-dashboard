# Sentinel 控制台

## 0. 概述

本项目是完整的Sentinel控制台，基于官方的**1.8.5**版本。Sentinel 控制台是流量控制、熔断降级规则统一配置和管理的入口，它为用户提供了机器自发现、簇点链路自发现、监控、规则配置等功能。在 Sentinel 控制台上，我们可以配置规则并实时查看流量控制效果。比较遗憾的是官方未提供对于zookeeper，nacos，apollo的直接的持久化支持，需要开发者进行二次开发才能使用，所以这里统一做成配置的方式，后续使用仅需配置参数传入即可。同时提供docker-compose部署的脚本和镜像支持。可以直接通过容器部署使用。

![img](https://user-images.githubusercontent.com/9434884/53381986-a0b73f00-39ad-11e9-90cf-b49158ae4b6f.png)

关于官方的介绍可以查看这里：[Sentinel 控制台](https://github.com/alibaba/Sentinel/tree/master/sentinel-dashboard)

## 1.关于持久化

首先，本代码基于两位大佬@FJiayang，@jnan806 的贡献，在此基础上简单修复了一下些小问题。在官方基础上支持了zookeeper，nacos，apollo的支持，如果不增加持久化参数就是默认(内存的方式)。通过配置参数注入，同时支持docker部署，提供docker-compose和对应的docker镜像。

更多持久化方式需要开发者自定义，这是官方给出的参考：[动态规则扩展](https://github.com/alibaba/Sentinel/wiki/%E5%8A%A8%E6%80%81%E8%A7%84%E5%88%99%E6%89%A9%E5%B1%95)

## 2.持久化的配置参数


项 | 类型 | 默认值 | 最小值 | 描述
--- | --- | --- | --- | ---
sentinel.dashboard.auth.username | String | sentinel | 无 | 登录控制台的用户名，默认为 `sentinel`
sentinel.dashboard.auth.password | String | sentinel | 无 | 登录控制台的密码，默认为 `sentinel`
sentinel.dashboard.app.hideAppNoMachineMillis | Integer | 0 | 60000 | 是否隐藏无健康节点的应用，距离最近一次主机心跳时间的毫秒数，默认关闭
sentinel.dashboard.removeAppNoMachineMillis | Integer | 0 | 120000 | 是否自动删除无健康节点的应用，距离最近一次其下节点的心跳时间毫秒数，默认关闭
sentinel.dashboard.unhealthyMachineMillis | Integer | 60000 | 30000 | 主机失联判定，不可关闭
sentinel.dashboard.autoRemoveMachineMillis | Integer | 0 | 300000 | 距离最近心跳时间超过指定时间是否自动删除失联节点，默认关闭
datasource.provider | String | memory | 无 | 默认为 `memory`, 可选持久化配置 `nacos`、`apollo`、`zookeeper`
datasource.provider.nacos.server-addr | String | localhost:8848 | 无 | nacos 注册中心地址
datasource.provider.nacos.username | String |  | 无 | nacos 用户名，默认为空
datasource.provider.nacos.password | String |  | 无 | nacos 密码，默认为空
datasource.provider.nacos.namespace | String |  | 无 | nacos 名臣空间，默认为空
datasource.provider.nacos.group-id | String | SENTINEL_GROUP | 无 | nacos 分组，默认为 `SENTINEL_GROUP`
datasource.provider.apollo.server-addr | String | http://localhost:10034 | 无 | apollo 注册中心地址，必须有前缀 `http://` 或 `https://`
datasource.provider.apollo.token | String | token | 无 | apollo 登录 token，默认为 `token`
datasource.provider.zookeeper.server-addr | String | localhost:2181 | 无 | zookeeper 注册中心地址
datasource.provider.zookeeper.session-timeout | Integer | 60000 | 0 | zookeeper session超时时间，默认 `60000`
datasource.provider.zookeeper.connection-timeout |  Integer | 15000 | 0 | zookeeper connection超时时间，默认 `15000`
datasource.provider.zookeeper.retry.max-retries | Integer | 3 | 0 | zookeeper 最大重试次数， 默认 `3`
datasource.provider.zookeeper.retry.base-sleep-time | Integer | 1000 | 1000 | zookeeper 重试间隔最小时长，默认 `1000`
datasource.provider.zookeeper.retry.max-sleep-time | Integer | 2147483647 | 0 | zookeeper 重试间隔最大时长，默认 `2147483647`


官方参考：[Sentinel 控制台功能介绍](https://github.com/alibaba/Sentinel/blob/master/sentinel-dashboard/Sentinel_Dashboard_Feature.md)。上面`datasource.provider`开头的都是新增的

## 3.控制台部署方式

### 方式一：源码编译与jar包部署

此方式适用于开发者对项目再次二次开发后使用。使用如下命令将代码打包成一个 jar:

```bash
mvn clean package -U -Dmaven.test.skip=true
```

编译后会得到一个jar包，对于jar包使用如下命令启动编译后的控制台：

```bash
java -Dserver.port=8858 \
-Dcsp.sentinel.dashboard.server=localhost:8858 \
-Dproject.name=sentinel-dashboard \
-jar sentinel-dashboard.jar
```

对于新加入的参数可以通过`-D`为前缀的方式注入。

### 方式二：docker-compose部署

下面以zookeeper作为持久化为例,对应的docker镜像地址参见：[sentinel-dashboard镜像](https://hub.docker.com/r/linking0606/sentinel-dashboard)

```yml
version: '3'
services:

  sentinel:
    image: linking0606/sentinel-dashboard:latest
    container_name: sentinel
    restart: always
    environment:
      - "PARAM=-Dserver.port=8858 -Dcsp.sentinel.dashboard.server=localhost:8858 -Dproject.name=sentinel-dashboard -Ddatasource.provider=zookeeper -Ddatasource.provider.zookeeper.server-addr=zk1:2181,zk2:2182,zk3:2183"
    ports:
      - "8858:8858"
    networks:
      - mynet

volumes:
  mydata:
    external: true
networks:
  mynet:
    driver: bridge
```

## 4.客户端接入的配置方式

对于sentinel的接入，下面以springcloud为例，主要是引入`spring cloud alibaba sentinel`的依赖,详细可以参考官方文档[Spring Cloud Alibaba Sentinel](https://github.com/alibaba/spring-cloud-alibaba/wiki/Sentinel)。

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-sentinel -->
<dependency>
  <groupId>com.alibaba.cloud</groupId>
  <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
  <version>2.2.4.RELEASE</version>
</dependency>
```

默认会将系统内所有的 URL 就自动成为 Sentinel 中的埋点资源，可以针对某个 URL 进行流控。

然后增加对于控制台地址的配置就好了

application.yml

```yml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8858 #sentinel-dashboard控制台的地址
```

如果使用基于内存的方式这样就完成了。

要想实现持久化配置，这里还是以zookeeper为例，需要引入对应依赖

```xml
<!--引入zookeeper-->
<dependency>
  <groupId>org.apache.zookeeper</groupId>
  <artifactId>zookeeper</artifactId>
  <version>3.4.14</version>
  <exclusions>
    <exclusion>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
    </exclusion>
    <exclusion>
      <artifactId>slf4j-api</artifactId>
      <groupId>org.slf4j</groupId>
    </exclusion>
    <exclusion>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<!--使用zookeeper作为sentinel的持久化选择，接入push模式的sentinel-->
<dependency>
  <groupId>com.alibaba.csp</groupId>
  <artifactId>sentinel-datasource-zookeeper</artifactId>
  <version>1.8.5</version>
  <exclusions>
    <exclusion>
      <artifactId>zookeeper</artifactId>
      <groupId>org.apache.zookeeper</groupId>
    </exclusion>
  </exclusions>
</dependency>
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-lang3</artifactId>
  <version>3.12.0</version>
</dependency>
```

然后增加对应的配置

```yml
# 这里定义了一个全局变量，为zookeeper的地址，请填写为您真是的地址
myurl:
  zk: 192.168.1.1:2181,192.168.1.2:2181,192.168.1.3:2181

spring:
  cloud:
    sentinel:
      datasource:
        #流控规则
        flow:
          zk:
            server-addr: ${myurl.zk}
            groupId: sentinel_rule_config/${spring.application.name}
            dataId: flow-rules
            # 规则类型，取值见：
            # org.springframework.cloud.alibaba.sentinel.datasource.RuleType
            rule-type: flow
        #熔断降级
        degrade:
          zk:
            server-addr: ${myurl.zk}
            groupId: sentinel_rule_config/${spring.application.name}
            dataId: degrade-rules
            rule-type: degrade
        #系统规则
        system:
          zk:
            server-addr: ${myurl.zk}
            groupId: sentinel_rule_config/${spring.application.name}
            dataId: system-rules
            rule-type: system
        #授权规则
        authority:
          zk:
            server-addr: ${myurl.zk}
            groupId: sentinel_rule_config/${spring.application.name}
            dataId: authority-rules
            rule-type: authority
        #参数规则
        param-flow:
          zk:
            server-addr: ${myurl.zk}
            groupId: sentinel_rule_config/${spring.application.name}
            dataId: param-rules
            rule-type: param-flow

```

更多接入方式可以参考官方文档：[主流框架的适配](https://github.com/alibaba/Sentinel/wiki/%E4%B8%BB%E6%B5%81%E6%A1%86%E6%9E%B6%E7%9A%84%E9%80%82%E9%85%8D#spring-cloud)

## 5.关于二次开发后的镜像制作
### 5.1完成开发后编译代码
```bash
mvn clean package -U -Dmaven.test.skip=true
```
### 5.2制作镜像
在DockerFile同级目录下执行下面步骤便完成了镜像的制作（这里准备上传到dockerhub仓库）
```bash
docker build -t 您的账户名称/sentinel-dashboard:latest .
```
### 5.3镜像上传到dockerhub
```bash
# 登录dockerhub
docker login --username=您的账户名称
# 上传镜像
docker push 您的账户名称/sentinel-dashboard:latest
```

