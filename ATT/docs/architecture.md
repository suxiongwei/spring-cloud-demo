# 微服务学习项目架构

## 分层架构
- API 网关层：`gateway` 提供统一入口、路由转发、JWT 鉴权与网关限流
- 业务服务层：`service-order`、`service-product`，含 OpenFeign 调用、负载均衡与熔断降级
- 基础设施层：注册配置（Nacos）、分布式事务（Seata）、消息队列（RocketMQ）、链路追踪（SkyWalking）

## 组件交互图
参见 `ATT/img` 中的示意图（Gateway 路由、Feign 调用、Sentinel、Seata 二阶段提交等）。

## 接口规范
- RESTful 资源路径：`/order/**`、`/product/**`
- OpenAPI：引入 `springdoc-openapi`，启动后访问 `http://<host>:<port>/swagger-ui.html`
- 统一响应结构：`ApiResponse`（成功/失败、消息、数据）

