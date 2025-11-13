# Sentinel 热点参数限流规则参数详解

本文档详细介绍了 Sentinel 热点参数限流规则中各个参数的含义和使用方法。

## 热点参数限流规则结构

热点参数限流规则是一个 JSON 数组，每个数组元素代表一条热点参数限流规则：

```json
[
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
]
```

## 参数详解

### 1. resource（资源名）
- **类型**: String
- **必填**: 是
- **说明**: 限流保护的资源名称，通常对应 `@SentinelResource` 注解中的 value 值
- **示例**: 
  ```json
  "resource": "hotspot-param"
  ```

### 2. limitApp（来源应用）
- **类型**: String
- **必填**: 是
- **说明**: 请求的来源应用名称，默认为 "default" 表示不区分来源
- **可选值**:
  - `"default"`: 不区分来源应用
  - `"其他应用名"`: 指定特定来源应用
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
- **说明**: 默认的限流阈值，针对参数索引位置的参数值
- **示例**:
  ```json
  "count": 5  // 默认每个参数值每秒最多访问5次
  ```

### 5. strategy（流控模式）
- **类型**: Integer
- **必填**: 是
- **说明**: 流控策略模式（热点参数限流中通常为0）
- **可选值**:
  - `0`: 直接限流
- **示例**:
  ```json
  "strategy": 0
  ```

### 6. controlBehavior（流控效果）
- **类型**: Integer
- **必填**: 是
- **说明**: 流量控制的效果（限流后的处理方式）
- **可选值**:
  - `0`: 快速失败（直接拒绝请求）
- **示例**:
  ```json
  "controlBehavior": 0  // 快速失败
  ```

### 7. paramIdx（参数索引）
- **类型**: Integer
- **必填**: 是
- **说明**: 热点参数的索引位置（从0开始）
- **示例**:
  ```json
  "paramIdx": 0  // 对第一个参数进行热点限流
  ```
  对于方法 `public String test(Long userId, Long productId)`：
  - `paramIdx: 0` 表示对 `userId` 参数限流
  - `paramIdx: 1` 表示对 `productId` 参数限流

### 8. paramFlowItemList（参数项限流列表）
- **类型**: Array
- **必填**: 否
- **说明**: 特殊参数值的限流配置列表，可以为特定参数值设置不同的限流阈值
- **结构**:
  ```json
  "paramFlowItemList": [
    {
      "object": "参数值",
      "classType": "参数类型",
      "count": 限流阈值
    }
  ]
  ```

#### 8.1 object（参数值）
- **类型**: String/Object
- **说明**: 特定的参数值
- **示例**:
  ```json
  "object": "1001"  // 参数值为1001
  ```

#### 8.2 classType（参数类型）
- **类型**: String
- **说明**: 参数值的Java类型全限定名
- **示例**:
  ```json
  "classType": "java.lang.Long"  // Long类型
  ```

#### 8.3 count（特殊参数限流阈值）
- **类型**: Double
- **说明**: 针对特定参数值的限流阈值
- **示例**:
  ```json
  "count": 2  // 该参数值每秒最多访问2次
  ```

## 规则示例解析

### 示例：热点参数限流
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
**解释**: 
- 对 `hotspot-param` 资源的第一个参数进行热点限流
- 默认每个参数值每秒最多访问 5 次（QPS）
- 特殊处理：参数值为 "1001" 的 Long 类型参数每秒最多访问 2 次

## 最佳实践

### 1. 合理选择参数索引
- 选择经常变化且需要限流的参数作为热点参数
- 常见场景：用户ID、商品ID、订单ID等

### 2. 设置合适的默认阈值
- 根据业务场景和系统承载能力设置合理的默认阈值
- 避免设置过低影响正常业务，过高起不到保护作用

### 3. 特殊参数值处理
- 为VIP用户、重要商品等设置特殊的限流阈值
- 为黑名单用户设置极低的阈值或直接拒绝

## 注意事项

1. **热点参数限流需要在代码中正确使用@SentinelResource注解**
2. **paramIdx必须与方法参数位置对应**
3. **classType必须是参数值的完整Java类型**
4. **特殊参数值的限流阈值会覆盖默认阈值**
5. **热点参数限流目前主要支持基本数据类型和String类型**