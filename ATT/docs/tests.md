# 测试用例文档

## 接口测试
- 使用 `ATT/http/requests.http` 或导入 Postman 集合（见 `ATT/postman/collection.json`）
- 网关路由：`/api/order/**`、`/api/product/**`，Header `X-Token` 示例
- JWT 鉴权：`Authorization: Bearer <jwt>`

## 熔断测试
- 触发慢调用：`/order/degrade/rt`（Sentinel Dashboard 设置 RT 阈值）
- 触发流控：`/order/rateLimit/qps`（设置 QPS 阈值）

## 事务一致性
- Seata 入口：`/purchase?userId=u1&commodityCode=c1&count=1`
- 观察 TC 日志与回滚（制造异常验证一致性）

## 链路追踪验证
- SkyWalking UI 中查看 `gateway → service-order → service-product` 的调用链

