# Sentinel 流控规则 Nacos 配置说明

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

## 配置步骤

### 1. 登录 Nacos 控制台
访问：http://127.0.0.1:8848/nacos
默认账号密码：nacos/nacos

### 2. 创建流控规则配置

在 Nacos 配置管理页面，点击【配置列表】→【+】按钮，填写以下信息：

- **Data ID**: `service-order-flow-rules`
- **Group**: `SENTINEL_GROUP`
- **配置格式**: `JSON`
- **配置内容**:

#### 方案一：基础限流规则（seckill 接口）

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

#### 方案二：关联限流规则（推荐，包含 writeDb 和 readDb 关联限流）

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
  },
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
]
```

#### 方案三：完整企业级限流策略（推荐）

详细配置请参考 [`sentinel-enterprise-strategies.md`](sentinel-enterprise-strategies.md) 文件，包含以下策略：

1. 接口级别限流（QPS和并发线程数）
2. 热点参数限流
3. 系统自适应保护
4. 关联限流
5. 链路限流
6. 熔断降级
7. 黑白名单控制

**示例配置：**
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
  },
  {
    "resource": "read-db",
    "limitApp": "default",
    "grade": 0,
    "count": 1,
    "strategy": 1,
    "refResource": "write-db",
    "controlBehavior": 0,
    "clusterMode": false
  },
  {
    "resource": "rateLimit-qps",
    "limitApp": "default",
    "grade": 1,
    "count": 10,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
  },
  {
    "resource": "rateLimit-thread",
    "limitApp": "default",
    "grade": 0,
    "count": 5,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
  }
]
```

**关联限流说明：**
- 当 `write-db` 资源的**并发线程数**超过 1 时
- 会触发对 `read-db` 资源的限流
- 这是典型的**写优先**场景：保护写操作，限制读操作

### 3. 配置字段说明

| 字段 | 说明 | 值 |
|-----|------|-----|
| resource | 资源名称，对应 @SentinelResource 的 value 值 | `seckill-order` / `read-db` |
| limitApp | 来源应用，default 表示不区分来源 | `default` |
| grade | 限流阈值类型，**0-线程数，1-QPS** | `0` (线程数) / `1` (QPS) |
| count | 限流阈值 | `1` |
| strategy | 流控模式，**0-直接，1-关联，2-链路** | `0` (直接) / `1` (关联) / `2` (链路) |
| refResource | 关联资源名称（仅 strategy=1 时生效） | `write-db` |
| controlBehavior | 流控效果，0-快速失败，1-Warm Up，2-排队等待 | `0` (快速失败) |
| clusterMode | 是否集群限流 | `false` |

#### 关联限流（strategy=1）特别说明

当配置为关联模式时：
- **resource**: 被限流的资源（受影响者）
- **refResource**: 关联的资源（触发者）
- **逆发：** 当 `refResource` 的流量超过阈值时，会限流 `resource`

**例子：**
```json
{
  "resource": "read-db",       // 被限流的是 readDb
  "refResource": "write-db",   // 触发条件是 writeDb
  "grade": 0,                   // 使用线程数
  "count": 1,                   // writeDb 并发线程数 > 1
  "strategy": 1                 // 关联模式
}
```
意思：当 `write-db` 的并发线程数超过 1 时，对 `read-db` 进行限流。

#### 链路限流（strategy=2）特别说明

当配置为链路模式时：
- 可以为同一资源在不同调用链路中设置不同的限流规则
- 需要通过 `limitApp` 字段区分不同的调用链路

#### 热点参数限流特别说明

热点参数限流需要单独配置，不使用 flow 规则类型：
- Data ID 格式：`${spring.application.name}-param-flow-rules`
- Group：`SENTINEL_GROUP`
- rule-type：`param-flow`

### 4. 验证配置

1. 启动 Sentinel Dashboard (默认端口 8080)
2. 启动 service-order 服务
3. 访问 Sentinel Dashboard，可以在【簇点链路】中看到资源
4. 测试限流效果

### 5. 测试接口

#### 测试 seckill 限流

```bash
# 正常请求
curl "http://localhost:8000/seckill?userId=1&productId=1000"

# 快速连续请求会触发限流，返回 fallback 方法的结果
```

#### 测试 writeDb 和 readDb 关联限流

**步骤 1：正常情况下访问**
```bash
# 访问 readDb，正常返回
curl "http://localhost:8000/readDb"
# 返回：readDb success...
```

**步骤 2：模拟 writeDb 高并发**
```bash
# 在一个终端中，让 writeDb 保持高并发（可以使用循环或并发工具）
while ($true) { 
    Start-Job -ScriptBlock { Invoke-WebRequest "http://localhost:8000/writeDb" } 
    Start-Sleep -Milliseconds 100 
}
```

**步骤 3：再次访问 readDb**
```bash
# 当 writeDb 并发超过 1 时，readDb 会被限流
curl "http://localhost:8000/readDb"
# 返回：系统繁忙，请稍后再试（writeDb 并发过高）...
```

#### 测试其他限流策略

详细测试方法请参考 [`sentinel-enterprise-strategies.md`](sentinel-enterprise-strategies.md) 文件，包含：

1. 接口级别限流测试（QPS和并发线程数）
2. 热点参数限流测试
3. 系统自适应保护测试
4. 链路限流测试
5. 熔断降级测试
6. 黑白名单控制测试

##### 测试 QPS 限流
```bash
# 使用 ab 工具进行并发测试（需要安装 Apache Bench）
ab -n 20 -c 5 http://localhost:8000/rateLimit/qps
```

##### 测试并发线程数限流
```powershell
# 使用 PowerShell 模拟并发请求
1..10 | ForEach-Object {
    Start-Job -ScriptBlock {
        Invoke-WebRequest "http://localhost:8000/rateLimit/thread"
    }
}
```

##### 测试热点参数限流
```bash
# 普通用户访问
curl "http://localhost:8000/hotspot/param?userId=1002&productId=2001"

# 特殊用户访问（限流更严格）
curl "http://localhost:8000/hotspot/param?userId=1001&productId=2001"
```

##### 测试熔断降级
```bash
# 连续访问慢接口触发熔断
for i in {1..10}; do
    curl "http://localhost:8000/degrade/rt"
    sleep 1
done
```

## 动态更新

在 Nacos 中修改流控规则配置后，服务会自动感知并更新，无需重启！

例如，将 count 改为 10：
```json
[
  {
    "resource": "seckill-order",
    "limitApp": "default",
    "grade": 1,
    "count": 10,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
  }
]
```

保存后立即生效，QPS 限制变为 10。

## 关联限流排查清单

如果关联限流未生效，请按以下步骤排查：

### ✅ 1. 检查 blockHandler 方法签名

**错误示例：**
```java
public String readDbBlockHandler(Throwable throwable) { ... }  // ❌ 错误
```

**正确示例：**
```java
public String readDbBlockHandler(BlockException ex) { ... }    // ✅ 正确
```

**注意：**
- blockHandler 方法参数类型必须是 `BlockException`，不能是 `Throwable`
- 必须导入：`import com.alibaba.csp.sentinel.slots.block.BlockException;`

### ✅ 2. 检查 Nacos 配置是否正确

在 Nacos 配置管理中确认：
- Data ID: `service-order-flow-rules`
- Group: `SENTINEL_GROUP`
- 配置格式: `JSON`
- JSON 格式正确，无语法错误

### ✅ 3. 验证规则是否推送成功

启动服务后，在 Sentinel Dashboard 中检查：
1. 进入【流控规则】菜单
2. 选择 `service-order` 服务
3. 查看是否存在 `read-db` 的关联规则
4. 确认关联资源是否为 `write-db`

### ✅ 4. 测试并发场景

**使用 PowerShell 模拟 writeDb 高并发：**
```powershell
# 创建测试脚本
1..10 | ForEach-Object {
    Start-Job -ScriptBlock {
        Invoke-WebRequest "http://localhost:8000/writeDb"
    }

# 等待 1 秒后测试 readDb
Start-Sleep -Seconds 1
Invoke-WebRequest "http://localhost:8000/readDb"
```

### ✅ 5. 查看日志输出

观察控制台日志：
- writeDb 被调用时应输出：`writeDb...`
- readDb 被限流时应输出：`readDb 被限流了，原因：writeDb 并发过高`

### ✅ 6. 关键配置检查

确认 `application.yml` 中包含：
```yaml
spring:
  cloud:
    sentinel:
      datasource:
        flow:
          nacos:
            server-addr: ${spring.cloud.nacos.server-addr}
            dataId: ${spring.application.name}-flow-rules
            groupId: SENTINEL_GROUP
            rule-type: flow
```

## 注意事项

1. 确保 Nacos 服务已启动（127.0.0.1:8848）
2. 确保 Sentinel Dashboard 已启动（localhost:8080）
3. Data ID 必须与 application.yml 中配置的一致：`${spring.application.name}-flow-rules`
4. Group 必须为：`SENTINEL_GROUP`
5. 配置格式必须选择：`JSON`
6. **blockHandler 方法参数类型必须是 `BlockException`**
7. **关联限流使用线程数（grade=0）更容易观察效果**
8. **writeDb 接口需要一定的耗时才能保持并发线程数**

## 常见问题

### Q1: 为什么 blockHandler 没有被调用？
A: 检查 blockHandler 方法签名，参数必须是 `BlockException` 类型。

### Q2: 关联限流不生效？
A: 
- 确认 writeDb 接口有足够的并发线程数（需要耗时操作）
- 确认 Nacos 中的 refResource 字段配置正确
- 在 Sentinel Dashboard 中查看实时监控

### Q3: 如何验证规则是否加载？
A: 启动服务后，在 Sentinel Dashboard 的【流控规则】中查看是否存在对应的规则。

### Q4: 如何模拟高并发？
A: 使用 PowerShell 的 `Start-Job` 命令或者使用 JMeter、ApacheBench 等压测工具。