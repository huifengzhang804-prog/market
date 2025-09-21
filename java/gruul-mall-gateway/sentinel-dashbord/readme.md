### 已开源

[开源地址 sentinel-dashboard-push](https://github.com/rowstop/sentinel-dashboard-push.git)

### PUSH 模式

![image](https://github.com/rowstop/rowstop-sentinel-dashboard-push/assets/100893704/0051dafb-fc89-47f7-9b02-f733aa3d99f6)

### sentinel-dashboard-nacos

push 模式， 实现了从 nacos拉取规则配置，支持新增、修改规则配置，由nacos下发规则配置到所有sentinel 客户端

#### 启动 `java -Dnacos.server-addr=127.0.0.1:8848 -Dnacos.namespace=public -jar sentinel-dashboard-nacos-1.8.7.jar`

#### 访问 127.0.0.1:8080

#### 网关配置

```yaml
spring:
  cloud:
    sentinel:
      eager: true
      transport:
        # dashboard的部署ip和端口 url
        dashboard: localhost:8080
        # 客户端ip 和端口 用于 dashboard同步数据
        # client-ip: 192.168.*.*
        # port: 8720
      datasource:
        gw-flow:
          nacos:
            server-addr: ${spring.cloud.nacos.server-addr}
            context-path: /nacos
            dataId: ${spring.application.name}-gw-flow.json
            groupId: SENTINEL
            namespace: ${spring.profiles.active}
            rule-type: GW_FLOW
        gw-degrade:
          nacos:
            server-addr: ${spring.cloud.nacos.server-addr}
            context-path: /nacos
            dataId: ${spring.application.name}-degrade.json
            groupId: SENTINEL
            namespace: ${spring.profiles.active}
            rule-type: DEGRADE
        gw-system:
          nacos:
            server-addr: ${spring.cloud.nacos.server-addr}
            context-path: /nacos
            dataId: ${spring.application.name}-system.json
            groupId: SENTINEL
            namespace: ${spring.profiles.active}
            rule-type: SYSTEM
```

#### sentinel 常见问题

[常见问题](https://github.com/alibaba/Sentinel/wiki/FAQ#q-sentinel-%E6%8E%A7%E5%88%B6%E5%8F%B0%E6%B2%A1%E6%9C%89%E6%98%BE%E7%A4%BA%E6%88%91%E7%9A%84%E5%BA%94%E7%94%A8%E6%88%96%E8%80%85%E6%B2%A1%E6%9C%89%E7%9B%91%E6%8E%A7%E5%B1%95%E7%A4%BA%E5%A6%82%E4%BD%95%E6%8E%92%E6%9F%A5)