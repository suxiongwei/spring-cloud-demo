# Dubbo 动态配置实现指南

## 概述

本项目已集成 Dubbo 3.x 的动态配置功能，使用 Nacos 作为配置中心，可以在运行时动态调整服务超时、重试等参数，无需重启服务。

## 配置架构

```
┌─────────────────────────────────────────────────────────┐
│                    Nacos 配置中心                         │
│  - Group: dubbo                                         │
│  - DataId: {serviceName}.configurators                 │
│  - 存储 Dubbo 动态配置规则（YAML 格式）                    │
└─────────────────────────────────────────────────────────┘
                            ↓
        ┌──────────────────────────────────────┐
        │   Dubbo Config Center Client         │
        │   - 监听配置变化                       │
        │   - 实时推送给应用                      │
        └──────────────────────────────────────┘
                            ↓
        ┌──────────────────────────────────────┐
        │   应用运行时动态生效                    │
        │   - 无需重启                           │
        │   - 下次 RPC 调用立即生效                │
        └──────────────────────────────────────┘
```

## 使用方式

### 方式一：通过管理接口（推荐）

#### 1. 设置服务级超时配置

```bash
# 针对特定服务接口设置超时时间为 5000ms
curl -X POST "http://localhost:8011/api/dubbo/config/service/timeout?serviceName=indi.mofan.product.dubbo.service.IProductDubboService&timeout=5000"
```

响应示例：
```json
{
  "success": true,
  "serviceName": "indi.mofan.product.dubbo.service.IProductDubboService",
  "timeout": 5000,
  "message": "配置发布成功，将在下次调用时生效"
}
```

#### 2. 设置应用级超时配置

```bash
# 针对整个应用设置超时时间
curl -X POST "http://localhost:8011/api/dubbo/config/app/timeout?appName=service-product-dubbo&timeout=6000"
```

#### 3. 查询当前配置

```bash
# 查询服务级配置
curl "http://localhost:8011/api/dubbo/config/query?dataId=indi.mofan.product.dubbo.service.IProductDubboService.configurators&group=dubbo"

# 查询应用级配置
curl "http://localhost:8011/api/dubbo/config/query?dataId=service-product-dubbo.configurators&group=dubbo"
```

#### 4. 删除配置规则

```bash
# 删除后将恢复使用代码中的配置
curl -X DELETE "http://localhost:8011/api/dubbo/config/delete?dataId=indi.mofan.product.dubbo.service.IProductDubboService.configurators&group=dubbo"
```

#### 5. 查看配置示例

```bash
curl "http://localhost:8011/api/dubbo/config/example"
```

### 方式二：直接在 Nacos 控制台配置

#### 1. 登录 Nacos 控制台

访问：http://172.29.64.1:8848/nacos

#### 2. 进入配置管理

- 命名空间：选择 `8699ba10-d5ae-4183-aa94-eef36789f4d3`
- 点击"配置列表" → "+"创建配置

#### 3. 创建服务级配置

**配置信息：**
- Data ID: `indi.mofan.product.dubbo.service.IProductDubboService.configurators`
- Group: `dubbo`
- 配置格式: `YAML`
- 配置内容:

```yaml
configVersion: v3.0
scope: service
key: indi.mofan.product.dubbo.service.IProductDubboService
enabled: true
configs:
  - side: provider
    parameters:
      timeout: 5000
      retries: 2
  - side: consumer
    parameters:
      timeout: 5000
      retries: 2
```

#### 4. 创建应用级配置

**配置信息：**
- Data ID: `service-product-dubbo.configurators`
- Group: `dubbo`
- 配置格式: `YAML`
- 配置内容:

```yaml
configVersion: v3.0
scope: application
key: service-product-dubbo
enabled: true
configs:
  - side: provider
    parameters:
      timeout: 6000
      retries: 3
      loadbalance: random
  - side: consumer
    parameters:
      timeout: 6000
      retries: 3
```

## 配置规则说明

### 配置项含义

| 字段 | 说明 | 必填 |
|------|------|------|
| configVersion | 配置版本，固定为 v3.0 | 是 |
| scope | 配置作用域：service（服务级）/ application（应用级） | 是 |
| key | 服务接口名或应用名 | 是 |
| enabled | 是否启用规则 | 是 |
| side | provider（提供者）/ consumer（消费者） | 是 |
| parameters | 具体的配置参数 | 是 |

### 支持的参数

| 参数 | 说明 | 示例 |
|------|------|------|
| timeout | 调用超时时间（毫秒） | 5000 |
| retries | 失败重试次数 | 2 |
| loadbalance | 负载均衡策略 | random / roundrobin / leastactive |
| cluster | 集群容错策略 | failover / failfast / failsafe |
| version | 服务版本 | 1.0.0 |
| group | 服务分组 | product |

## 配置优先级

Dubbo 配置优先级（从高到低）：

1. **方法级配置**（注解中的 timeout 参数）
2. **接口级动态配置**（Nacos 中服务级配置）
3. **应用级动态配置**（Nacos 中应用级配置）
4. **全局配置**（application.yml 中的配置）

## 测试验证

### 1. 测试超时配置

```bash
# 1. 先调用一次，观察默认超时时间
curl "http://localhost:8001/api/order/demo/dubbo/timeout?productId=1&sleepTime=2000"

# 2. 设置超时为 1500ms（小于休眠时间，会超时）
curl -X POST "http://localhost:8011/api/dubbo/config/service/timeout?serviceName=indi.mofan.product.dubbo.service.IProductDubboService&timeout=1500"

# 3. 等待 3-5 秒让配置生效

# 4. 再次调用，应该会超时
curl "http://localhost:8001/api/order/demo/dubbo/timeout?productId=1&sleepTime=2000"

# 5. 设置超时为 5000ms（大于休眠时间，不会超时）
curl -X POST "http://localhost:8011/api/dubbo/config/service/timeout?serviceName=indi.mofan.product.dubbo.service.IProductDubboService&timeout=5000"

# 6. 再次调用，应该成功
curl "http://localhost:8001/api/order/demo/dubbo/timeout?productId=1&sleepTime=2000"
```

### 2. 查看日志验证

在消费端（service-order-dubbo）日志中可以看到：

```
通过Dubbo调用模拟超时，ID: 1, 休眠: 2000ms
消费端配置: version=1.0.0, group=product, timeout=1500  // 动态配置生效
```

## 配置管理最佳实践

### 1. 配置层次选择

- **服务级配置**：适用于对特定接口有特殊要求的场景
- **应用级配置**：适用于对整个应用统一调整的场景
- **全局配置**：作为默认值，在代码中配置

### 2. 配置变更流程

1. **灰度发布**：先在测试环境验证
2. **监控观察**：发布后观察调用成功率、超时率等指标
3. **回滚准备**：配置不当可立即删除配置恢复默认值

### 3. 配置命名规范

- 服务级：`{完整接口名}.configurators`
- 应用级：`{应用名}.configurators`
- Group 统一使用：`dubbo`

### 4. 监控和告警

建议配置以下监控指标：
- Dubbo 调用超时率
- 配置变更记录
- 服务调用 P99 延迟

## 故障排查

### 配置不生效？

1. **检查配置中心连接**
```bash
# 查看应用日志
grep "config-center" logs/application.log
```

2. **确认配置格式正确**
   - YAML 格式严格要求缩进
   - key 必须与服务接口名或应用名完全匹配

3. **检查命名空间**
   - 确保配置在正确的命名空间下

4. **查看 Dubbo 内部配置**
```bash
# 通过 QOS 查看运行时配置
telnet 127.0.0.1 22222
getConfig
```

### 配置冲突？

如果同时存在多个配置，优先级顺序：
1. 方法级注解配置
2. 接口级动态配置
3. 应用级动态配置
4. 全局 YAML 配置

## 相关文档

- [Dubbo 官方文档 - 超时配置](https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/tasks/traffic-management/timeout/)
- [Nacos 配置中心](https://nacos.io/zh-cn/docs/config-center.html)
- [Dubbo 配置中心](https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/config-center/)

## 注意事项

1. **配置生效时间**：配置发布后，会在下一次 RPC 调用时立即生效，无需重启
2. **配置持久化**：配置存储在 Nacos 中，应用重启后仍然有效
3. **配置删除**：删除配置后会恢复使用代码中的默认配置
4. **性能影响**：动态配置对性能影响极小，配置监听采用长轮询机制
5. **安全性**：生产环境建议限制配置管理接口的访问权限
