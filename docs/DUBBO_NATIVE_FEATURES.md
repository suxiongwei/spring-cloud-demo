# Dubbo原生容错机制说明

本项目的Dubbo服务已移除Sentinel集成，改用Dubbo原生的容错和限流能力。

## 🎯 架构对比

### 移除前（Dubbo + Sentinel）
```
请求 → Sentinel限流 → Sentinel熔断 → Dubbo调用 → 响应
```

### 移除后（纯Dubbo原生）
```
请求 → Dubbo超时/重试 → 业务降级处理 → 响应
```

---

## 📋 配置详解

### service-product-dubbo（服务提供者）

```yaml
dubbo:
  provider:
    timeout: 3000                # 调用超时3秒
    retries: 1                   # 失败重试1次（不含首次）
    loadbalance: random          # 随机负载均衡
    threads: 200                 # 最多200个工作线程
    queues: 0                    # 无队列，超过线程数直接拒绝
    cluster: failover            # 故障转移模式
```

**工作原理**：
- 请求到达 → 检查是否有可用线程
- 有线程 → 处理请求
- 无线程（200个已占满） → 直接拒绝新请求，防止堆积

### service-order-dubbo（服务消费者）

```yaml
dubbo:
  consumer:
    timeout: 3000                # 调用超时3秒
    retries: 1                   # 失败重试1次
    check: false                 # 启动时不检查服务可用性
    loadbalance: random          # 随机选择服务提供者
    cluster: failover            # 故障自动转移到其他节点
```

**工作原理**：
- 消费者发起调用
- 等待响应（最多3秒）
- 超时或异常 → 自动重试1次
- 仍失败 → 返回异常，由业务代码处理

---

## 🛡️ 容错机制详解

### 1. 超时控制（timeout: 3000）

**场景**：库存服务查询变慢

```
T0: 订单服务调用库存服务
T1: 库存服务处理中...
T2: 库存服务处理中...
T3: 库存服务处理中...（已耗时3秒，Dubbo超时触发）
→ Dubbo立即返回超时异常
→ 订单服务捕获异常，返回降级值
```

**优势**：
- 防止线程永久堆积
- 快速失败，用户可快速重试
- 不耗尽系统资源

### 2. 重试机制（retries: 1）

**场景**：临时网络抖动

```
第1次调用 → 网络抖动，异常 ❌
T0: Dubbo自动重试
第2次调用 → 网络恢复，成功 ✅
```

**注意**：
- retries=1表示失败后再试1次（总共2次调用）
- 若提供者有多个实例，重试会转移到其他实例
- 对于已改变状态的操作（如下单），应设置retries=0

### 3. 负载均衡（loadbalance: random）

**可选策略**：
- **random**：随机选择（默认）
- **roundrobin**：轮询选择
- **leastactive**：选择活跃度最低的

**例子**：消费者连接两个产品服务实例

```
消费者 → [Product-1, Product-2]
         ↘         ↙
        随机选择其中一个
```

### 4. 集群容错（cluster: failover）

**可选策略**：
- **failover**：失败转移（推荐）- 尝试其他节点
- **failfast**：快速失败 - 立即返回异常
- **failsafe**：失败安全 - 返回空值
- **failback**：失败自动恢复 - 定时重试
- **forking**：并行调用 - 多个节点同时调用

**failover工作流程**：

```
消费者 → Product-1 (失败)
        ↘ Product-2 (重试)
           ✅ 成功
```

---

## 💼 业务级降级处理

消费端（ProductDubboClient）已添加异常捕获和降级：

```java
public Product getProduct(Long productId) {
    try {
        // 正常调用
        return productDubboService.getProductById(productId);
    } catch (Exception e) {
        // 降级处理：返回默认值
        log.warn("获取产品失败，返回降级值");
        return Product.builder()
                .id(productId)
                .name("产品模拟值")
                .price(0.0)
                .build();
    }
}
```

**降级策略**：
- 返回缓存值（如果有）
- 返回默认值/模拟值
- 返回空列表/空对象
- 返回友好的错误提示

---

## 📊 对比表：Dubbo原生 vs Sentinel

| 能力 | Dubbo原生 | Sentinel | 适用场景 |
|------|---------|---------|--------|
| **超时控制** | ✅ 3000ms | ❌ N/A | 防止无限等待 |
| **重试机制** | ✅ failover | ❌ N/A | 临时故障恢复 |
| **线程隔离** | ✅ threads:200 | ❌ N/A | 防止资源耗尽 |
| **负载均衡** | ✅ 多种策略 | ❌ N/A | 分散流量 |
| **QPS限流** | ❌ 无 | ✅ 有 | 防止高峰压垮 |
| **熔断降级** | ❌ 无 | ✅ 有 | 自动快速停止 |
| **黑白名单** | ❌ 无 | ✅ 有 | 精细访问控制 |
| **可视化管理** | ❌ 无 | ✅ 有 | 运维监控 |

---

## 🔧 生产环境建议

### 推荐配置

```yaml
# 产品服务（提供者）
dubbo:
  provider:
    timeout: 5000           # 根据实际响应时间调整
    retries: 1              # 防止重试导致响应变慢
    threads: 300            # 根据并发数调整
    queues: 0               # 快速拒绝，不堆积
    cluster: failover       # 自动转移到其他节点

# 订单服务（消费者）
dubbo:
  consumer:
    timeout: 5000           # 与提供者一致
    retries: 1              # 重要操作设为0
    loadbalance: random     # 或 leastactive
    cluster: failover       # 故障自动转移
```

### 关键操作特殊处理

```java
// 下单（改变状态）：不能重试
@DubboReference(retries = 0)  // 禁止重试
private OrderService orderService;

// 查询（幂等）：可以重试
@DubboReference(retries = 2)  // 可以重试
private QueryService queryService;
```

---

## 📈 性能对比

### 测试场景：库存查询延迟

| 配置 | 响应时间 | 特点 |
|------|---------|------|
| Dubbo原生 | 3000ms + 100ms(重试) | 快速失败，清晰的降级时机 |
| Dubbo + Sentinel | 500ms(熔断触发) | 毫秒级快速停止 |

**选择**：
- 若需要毫秒级响应 → 使用Sentinel
- 若能接受秒级降级 → 使用Dubbo原生
- **最佳实践** → 两者结合

---

## ⚠️ 常见问题

### Q1: 为什么设置`retries=1`而不是更多？

A: 重试次数过多会导致响应变慢：
```
调用1（失败）+ 调用2（失败）+ 调用3（失败） = 9秒总耗时 ❌
调用1（失败）+ 调用2（成功）= 6秒总耗时 ✅
```

### Q2: 为什么线程池设置`queues=0`？

A: 防止请求堆积：
```
threads=200满 + 新请求来了
→ 若有队列：排队等待（堆积） ❌
→ 若无队列：直接拒绝（快速失败） ✅
```

### Q3: 业务代码中异常捕获有必要吗？

A: **必要**。Dubbo的容错只能保证不死人，但无法给出有意义的降级：
```java
// Dubbo只能超时返回异常
try {
    dubboService.call();
} catch (TimeoutException e) {
    // 业务级降级：返回缓存值、默认值等
    return getCachedValue();  // ← 这个必须由业务代码实现
}
```

---

## 📌 总结

| 特性 | Dubbo原生 | 评价 |
|------|---------|------|
| **性能** | ⭐⭐⭐⭐⭐ | TCP直连，极高效率 |
| **容错** | ⭐⭐⭐⭐ | 超时/重试/转移，足够用 |
| **易用性** | ⭐⭐⭐ | 需要手写降级代码 |
| **可观测** | ⭐⭐ | 依赖日志查看 |
| **学习成本** | ⭐⭐⭐⭐ | 要理解多个概念 |

**当前项目配置**：✅ 生产级别可用，轻量级方案

---

## 参考资源

- [Apache Dubbo官方文档](https://dubbo.apache.org/zh-cn/docs/)
- [Dubbo容错策略](https://dubbo.apache.org/zh-cn/docs/v2.7/user/examples/fault-tolerent-strategy/)
- [线程池配置](https://dubbo.apache.org/zh-cn/docs/v2.7/user/examples/thread-pool/)
