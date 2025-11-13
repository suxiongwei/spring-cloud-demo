# Sentinel 流控规则参数详解

本文档详细介绍了 Sentinel 流控规则中各个参数的含义和使用方法。

## 流控规则结构

流控规则是一个 JSON 数组，每个数组元素代表一条流控规则：

```json
[
  {
    "resource": "seckill-order",
    "limitApp": "default",
    "grade": 1,
    "count": 1,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
  }
]
```

## 参数详解

### 1. resource（资源名）
- **类型**: String
- **必填**: 是
- **说明**: 限流保护的资源名称，通常对应 `@SentinelResource` 注解中的 value 值或 URL 路径
- **示例**: 
  ```json
  "resource": "seckill-order"
  ```

### 2. limitApp（来源应用）
- **类型**: String
- **必填**: 是
- **说明**: 请求的来源应用名称，默认为 "default" 表示不区分来源
- **可选值**:
  - `"default"`: 不区分来源应用
  - `"其他应用名"`: 指定特定来源应用
  - `"chain-A"`: 指定调用链路来源
- **示例**:
  ```json
  "limitApp": "default"
  ```

### 3. grade（限流阈值类型）
- **类型**: Integer
- **必填**: 是
- **说明**: 限流的阈值类型
- **可选值**:
  - `0`: 线程数（并发线程数限流）
  - `1`: QPS（每秒请求数限流）
- **示例**:
  ```json
  "grade": 1  // QPS限流
  ```

### 4. count（限流阈值）
- **类型**: Double
- **必填**: 是
- **说明**: 限流阈值，具体含义根据 grade 决定
- **示例**:
  ```json
  "count": 10  // QPS阈值为10，或并发线程数阈值为10
  ```

### 5. strategy（流控模式）
- **类型**: Integer
- **必填**: 是
- **说明**: 流控策略模式
- **可选值**:
  - `0`: 直接限流（直接对当前资源限流）
  - `1`: 关联限流（关联资源触发时对当前资源限流）
  - `2`: 链路限流（针对特定调用链路限流）
- **示例**:
  ```json
  "strategy": 1  // 关联限流模式
  ```

### 6. refResource（关联资源名）
- **类型**: String
- **必填**: 否（仅 strategy=1 时需要）
- **说明**: 关联限流时指定的关联资源名称
- **示例**:
  ```json
  "refResource": "write-db"  // 当write-db资源被访问时，对当前资源限流
  ```

### 7. controlBehavior（流控效果）
- **类型**: Integer
- **必填**: 是
- **说明**: 流量控制的效果（限流后的处理方式）
- **可选值**:
  - `0`: 快速失败（直接拒绝请求）
  - `1`: Warm Up（预热启动）
  - `2`: 排队等待（匀速器模式）
- **示例**:
  ```json
  "controlBehavior": 0  // 快速失败
  ```

### 8. clusterMode（是否集群限流）
- **类型**: Boolean
- **必填**: 是
- **说明**: 是否启用集群限流模式
- **可选值**:
  - `true`: 启用集群限流
  - `false`: 单机限流
- **示例**:
  ```json
  "clusterMode": false  // 单机限流
  ```

## 规则示例解析

### 示例1：秒杀接口QPS限流
```json
{
  "resource": "seckill-order",
  "limitApp": "default",
  "grade": 1,
  "count": 1,
  "strategy": 0,
  "controlBehavior": 0,
  "clusterMode": false
}
```
**解释**: 对 `seckill-order` 资源进行 QPS 限流，阈值为 1，即每秒最多处理 1 个请求，超出直接拒绝。

### 示例2：关联限流
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
**解释**: 当 `write-db` 资源的并发线程数超过 1 时，对 `read-db` 资源进行限流。

### 示例3：QPS限流示例
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
**解释**: 对 `rateLimit-qps` 资源进行 QPS 限流，阈值为 10，即每秒最多处理 10 个请求。

### 示例4：并发线程数限流
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
**解释**: 对 `rateLimit-thread` 资源进行并发线程数限流，最多同时处理 5 个请求。

### 示例5：链路限流（默认链路）
```json
{
  "resource": "common-resource",
  "limitApp": "default",
  "grade": 1,
  "count": 20,
  "strategy": 0,
  "controlBehavior": 0,
  "clusterMode": false
}
```
**解释**: 对来自默认链路的 `common-resource` 资源进行 QPS 限流，阈值为 20。

### 示例6：链路限流（特定链路）
```json
{
  "resource": "common-resource",
  "limitApp": "chain-A",
  "grade": 1,
  "count": 10,
  "strategy": 0,
  "controlBehavior": 0,
  "clusterMode": false
}
```
**解释**: 对来自 `chain-A` 链路的 `common-resource` 资源进行 QPS 限流，阈值为 10。

## 最佳实践

### 1. 选择合适的限流阈值类型
- **QPS限流**：适用于高频短时接口，如API网关
- **线程数限流**：适用于耗时操作，如数据库查询

### 2. 合理设置流控模式
- **直接限流**：最常见的限流方式
- **关联限流**：适用于读写分离等场景
- **链路限流**：适用于不同调用链路需要不同限流策略的场景

### 3. 选择合适的流控效果
- **快速失败**：适用于对响应时间敏感的场景
- **Warm Up**：适用于冷启动需要预热的场景
- **排队等待**：适用于需要严格控制请求处理速率的场景

## 注意事项

1. **resource名称必须与代码中的@SentinelResource注解value值一致**
2. **关联限流需要正确设置refResource参数**
3. **链路限流需要在application.yml中设置spring.cloud.sentinel.web-context-unify=false**
4. **集群限流需要配置Sentinel集群服务端**