# 嵌入式日志监控

> 只能在tomcat8.0+或者servlet3.0+以上使用

SpringBoot版本2.5.2

将前端页面放到静态资源目录下（注意不要在引用jar包的项目的pom中，配置resource exclude，否则读取不到）

如果路由与主项目自带的静态资源路由重复，会优先显示主项目静态资源，所以避免重复，统一放到logmonitor路由下

```
src\main\resources\META-INF\resources\logmonitor
```

执行命令，将jar包安装到仓库

```
mvn clean install
```

任意项目想要嵌入这个功能，只需要添加依赖即可，因为有些jar包是重复的，所以exclude掉。

```xml
<dependency>
    <groupId>top.meethigher</groupId>
    <artifactId>log-monitor-embedded</artifactId>
    <version>1.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </exclusion>
        <exclusion>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
        </exclusion>
        <exclusion>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

然后在被嵌入项目的配置文件中，如application.yml中，添加


```yaml
server:
  port: 11000
  servlet:
    context-path:
cas:
  server-name: http://192.168.110.199:11000
spring:
  mvc:
    static-path-pattern: /**
# log-monitor项目自有的配置，必须配
log:
  # websocket监听文件变化的请求路径
  websocket: /monitor
  monitor:
    # logback配置的默认目录
    defaultPath: D:/aaa
    # logback生成的实时日志路径，最终的监控日志路径是defaultPath与defaultLog拼接
    defaultLog: /system-info.log
    # true： 按照文件更新时间查询 false：按照文件名称的时间查询（选择false时，必须严格按照logback示例配置来，否则查不到）
    queryByFileTime: false
  # 配置集群的所有节点
  clusterNode: 192.168.110.199:10000,192.168.110.199:11000,192.168.110.199:12000
```
如果选择了queryByFileTime为false，logback需要严格按照下面的logback这种形式来配置，比如配置log-error

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    <fileNamePattern>${DEFAULT_PATH}/log-error-%d{yyyy-MM-dd,aux}/log-error-%d{yyyy-MM-dd-HH}.log</fileNamePattern>
</rollingPolicy>
```



