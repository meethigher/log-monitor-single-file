# 日志监控

springboot版本2.5.2

```yaml
server:
  port: 10000
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
## 接口

项目启动，访问http://localhost:10000/swagger-ui/index.html

## websocket接口

一个session对应一个监控线程，session断开线程关闭

请求格式

```
ws://127.0.0.1:10000/monitor?logPath=要实时监控的文件路径
```

如

```
ws://127.0.0.1:10000/monitor?logPath=D:/logback/test2.txt
```



