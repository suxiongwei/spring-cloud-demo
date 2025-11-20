# 组件配置说明

## Nacos
- 多环境命名空间：`dev/test/prod`，示例见各 `application.yml` 的 `spring.config.import`
- 动态刷新：`@RefreshScope` 应用于 `OrderProperties`，`/config` 接口验证

## Sentinel
- 服务侧：`service-order` 配置 Nacos 规则数据源（流控/热点/熔断/系统/授权）
- 网关侧：`gateway` 引入 `spring-cloud-alibaba-sentinel-gateway`，配置 `gw-flow` 与 `gw-api-group` 规则

## OpenFeign + LoadBalancer
- 声明式接口：`ProductFeignClient`
- 负载均衡：`@LoadBalanced RestTemplate` 与默认轮询策略测试 `LoadBalancerTest`
- 熔断降级：Fallback `ProductFeignClientFallback`

## Seata（AT 模式）
- 典型流程：`seata-business` → 扣减库存（storage）→ 创建订单（order），入口 `/purchase`
- 事务注解：`@GlobalTransactional`，配置文件 `file.conf` 与数据脚本 `ATT/sql/seata.sql`

## RocketMQ
- 生产者：`service-order` 的 `/event/send` 与 `/event/sendTx`
- 事务监听：`OrderEventTxListener`
- 消费者与死信：`service-product` 的 `OrderEventConsumer` 与 `OrderEventDlqConsumer`

## SkyWalking
- OAP/UI 部署后，服务以 Java Agent 方式接入，观察全链路与性能指标
- 告警规则：在 OAP 中配置阈值（RT、错误率、QPS）并绑定通知渠道

