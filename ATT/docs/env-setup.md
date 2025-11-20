# 环境搭建手册

## 基础组件
- Nacos：`docker run -p 8848:8848 nacos/nacos-server:latest`
- Sentinel Dashboard：`docker run -p 8080:8080 bladex/sentinel-dashboard`
- RocketMQ：NameServer `9876`、Broker `10911`
- SkyWalking：OAP 与 UI，参见官方镜像 `apache/skywalking-oap-server`、`apache/skywalking-ui`

## Java Agent（SkyWalking）
- 启动参数：`-javaagent:/path/skywalking-agent.jar -Dskywalking.agent.service_name=<service>`
- 日志包含 traceId：使用 Logstash Encoder 输出 JSON，集中采集至 ELK

## Kubernetes 部署
- 清单位于 `deploy/k8s/` 目录，包含：Gateway、Order、Product 的 Deployment/Service 与健康检查
- 使用 `kubectl apply -f deploy/k8s/` 部署

