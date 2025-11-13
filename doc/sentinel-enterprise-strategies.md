# 企业级 Sentinel 限流策略配置指南

本文档详细介绍了在微服务架构下，如何通过 Sentinel + Nacos 实现动态、精细化的流量治理，确保服务稳定性和高可用性。

## 配置文件说明

本项目为每种规则类型创建独立的配置文件：

1. `service-order-flow-rules.json` - 流控规则
2. `service-order-param-flow-rules.json` - 热点参数限流规则
3. `service-order-degrade-rules.json` - 熔断降级规则
4. `service-order-system-rules.json` - 系统规则
5. `service-order-authority-rules.json` - 授权规则

**优势：**
- 职责分离，每种规则类型独立管理
- 便于维护，可以单独更新某类规则而不影响其他规则
- 灵活配置，不同规则可以有不同的更新频率

这些文件可以直接导入到 Nacos 中使用，对应的 Data ID 和 Group 如下：

| 配置文件 | Data ID | Group |
|---------|---------|-------|
| 流控规则 | service-order-flow-rules | SENTINEL_GROUP |
| 热点参数限流规则 | service-order-param-flow-rules | SENTINEL_GROUP |
| 熔断降级规则 | service-order-degrade-rules | SENTINEL_GROUP |
| 系统规则 | service-order-system-rules | SENTINEL_GROUP |
| 授权规则 | service-order-authority-rules | SENTINEL_GROUP |

每种规则类型的详细参数说明请参考对应的文档：
- [流控规则参数详解](sentinel-flow-rules-documentation.md)
- [热点参数限流规则参数详解](sentinel-param-flow-rules-documentation.md)
- [熔断降级规则参数详解](sentinel-degrade-rules-documentation.md)
- [系统规则参数详解](sentinel-system-rules-documentation.md)
- [授权规则参数详解](sentinel-authority-rules-documentation.md)
## 1. 接口级别限流

### 1.1 QPS限流

**适用场景：**
- 秒杀接口等需要严格控制访问频率的场景
- 防止突发流量冲击系统

**Nacos配置示例：**
```json
{
  "resource": "rateLimit-qps",
  "limitApp": "default",
  "grade": 1,
  "count": 10,
  "strategy": 0,
  "controlBehavior": 0,
  "clusterMode": false
}
```

**参数说明：**
- `grade`: 1 表示 QPS 限流
- `count`: 10 表示每秒最多处理 10 个请求
- `strategy`: 0 表示直接限流
- `controlBehavior`: 0 表示快速失败

### 1.2 并发线程数限流

**适用场景：**
- 数据库连接池有限、需要控制并发处理数量的场景
- 保护系统资源不被耗尽

**Nacos配置示例：**
```json
{
  "resource": "rateLimit-thread",
  "limitApp": "default",
  "grade": 0,
  "count": 5,
  "strategy": 0,
  "controlBehavior": 0,
  "clusterMode": false
}
```

**参数说明：**
- `grade`: 0 表示并发线程数限流
- `count`: 5 表示最多同时处理 5 个请求

## 2. 热点参数限流

**适用场景：**
- 根据用户ID、商品ID等参数进行精细化流量控制
- 保护热点数据不被过度访问

**Nacos配置示例：**
```json
{
  "resource": "hotspot-param",
  "limitApp": "default",
  "grade": 1,
  "count": 5,
  "strategy": 0,
  "controlBehavior": 0,
  "paramIdx": 0,
  "paramFlowItemList": [
    {
      "object": "1001",
      "classType": "java.lang.Long",
      "count": 2
    }
  ]
}
```

**参数说明：**
- `paramIdx`: 0 表示对第一个参数（userId）进行限流
- `count`: 5 表示普通用户每秒最多访问 5 次
- `paramFlowItemList`: 特殊参数限流配置
  - `object`: "1001" 表示用户ID为1001的特殊用户
  - `count`: 2 表示该用户每秒最多访问 2 次

## 3. 系统自适应保护

**适用场景：**
- 基于系统负载自动调节流量，保护系统稳定性
- 防止系统过载导致雪崩效应

**Nacos配置示例：**
```json
{
  "highestSystemLoad": 2.0,
  "qps": -1,
  "avgRt": -1,
  "maxThread": -1,
  "highestCpuUsage": 0.8
}
```

**参数说明：**
- `highestSystemLoad`: 系统负载阈值，超过2.0时触发保护
- `highestCpuUsage`: CPU使用率阈值，超过80%时触发保护

## 4. 关联限流

**适用场景：**
- 写操作优先于读操作的场景
- 保护写操作，限制读操作

**Nacos配置示例：**
```json
{
  "resource": "read-db",
  "limitApp": "default",
  "grade": 0,
  "count": 1,
  "strategy": 1,
  "refResource": "write-db",
  "controlBehavior": 0,
  "clusterMode": false
}
```

**参数说明：**
- `strategy`: 1 表示关联限流
- `refResource`: "write-db" 表示关联的资源
- 当 `write-db` 的并发线程数超过 1 时，对 `read-db` 进行限流

## 5. 链路限流

**适用场景：**
- 同一资源在不同调用链路中采用不同限流策略
- 精细化控制不同业务场景下的资源访问

**Nacos配置示例：**
```json
[
  {
    "resource": "common-resource",
    "limitApp": "default",
    "grade": 1,
    "count": 20,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
  },
  {
    "resource": "common-resource",
    "limitApp": "chain-A",
    "grade": 1,
    "count": 10,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
  }
]
```

**参数说明：**
- 第一条规则：默认情况下，每秒最多处理 20 个请求
- 第二条规则：来自链路A的请求，每秒最多处理 10 个请求

## 6. 熔断降级

**适用场景：**
- 依赖服务不稳定时的快速失败和恢复机制
- 防止故障扩散，提高系统可用性

**Nacos配置示例：**
```json
{
  "resource": "degrade-rt",
  "grade": 0,
  "count": 1000,
  "timeWindow": 10,
  "minRequestAmount": 5,
  "statIntervalMs": 10000,
  "slowRatioThreshold": 0.6
}
```

**参数说明：**
- `grade`: 0 表示慢调用比例熔断
- `count`: 1000 表示慢调用阈值（毫秒）
- `timeWindow`: 10 表示熔断持续时间（秒）
- `minRequestAmount`: 5 表示最小请求数
- `statIntervalMs`: 10000 表示统计时间窗口（毫秒）
- `slowRatioThreshold`: 0.6 表示慢调用比例阈值

## 7. 黑白名单控制

**适用场景：**
- 基于来源IP或用户标识的访问控制
- 防止恶意访问，保护系统安全

**Nacos配置示例：**
```json
[
  {
    "resource": "authority-control",
    "limitApp": "white_list",
    "strategy": 0
  },
  {
    "resource": "authority-control",
    "limitApp": "black_list",
    "strategy": 1
  }
]
```

**参数说明：**
- 第一条规则：允许白名单应用访问
- 第二条规则：拒绝黑名单应用访问
- `strategy`: 0 表示白名单，1 表示黑名单

## 测试方法

### 1. QPS限流测试
```bash
# 使用ab工具进行并发测试
ab -n 20 -c 5 http://localhost:8000/rateLimit/qps
```

### 2. 并发线程数限流测试
```powershell
# 使用PowerShell模拟并发请求
1..10 | ForEach-Object {
    Start-Job -ScriptBlock {
        Invoke-WebRequest "http://localhost:8000/rateLimit/thread"
    }
}
```

### 3. 热点参数限流测试
```bash
# 普通用户访问
curl "http://localhost:8000/hotspot/param?userId=1002&productId=2001"

# 特殊用户访问（限流更严格）
curl "http://localhost:8000/hotspot/param?userId=1001&productId=2001"
```

### 4. 熔断降级测试
```bash
# 连续访问慢接口触发熔断
for i in {1..10}; do
    curl "http://localhost:8000/degrade/rt"
    sleep 1
done
```

## 最佳实践

### 1. 配置管理
- 使用 Nacos 统一管理所有 Sentinel 规则
- 通过命名空间隔离不同环境的配置
- 定期备份重要规则配置

### 2. 监控告警
- 集成 Sentinel Dashboard 实时监控
- 设置关键指标告警（如限流次数、熔断触发等）
- 定期分析流量模式，优化规则配置

### 3. 规则优化
- 根据业务特点调整限流阈值
- 定期评估规则有效性
- 建立规则变更流程，确保变更可控

### 4. 故障处理
- 建立完善的 fallback 机制
- 记录详细的限流日志，便于问题排查
- 制定应急预案，快速响应突发情况

通过合理配置这些限流策略，可以有效保障微服务系统的稳定性和高可用性。