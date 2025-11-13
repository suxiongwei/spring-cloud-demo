# Sentinel 授权规则参数详解

本文档详细介绍了 Sentinel 授权规则中各个参数的含义和使用方法。

## 授权规则结构

授权规则是一个 JSON 数组，每个数组元素代表一条授权规则：

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

## 参数详解

### 1. resource（资源名）
- **类型**: String
- **必填**: 是
- **说明**: 授权控制的资源名称，通常对应 `@SentinelResource` 注解中的 value 值
- **示例**: 
  ```json
  "resource": "authority-control"
  ```

### 2. limitApp（来源应用）
- **类型**: String
- **必填**: 是
- **说明**: 请求的来源应用名称
- **特殊值**:
  - `"default"`: 默认来源（未指定来源的应用）
  - `"其他应用名"`: 特定来源应用
  - `"white_list"`: 白名单标识
  - `"black_list"`: 黑名单标识
- **示例**:
  ```json
  "limitApp": "white_list"
  ```

### 3. strategy（授权策略）
- **类型**: Integer
- **必填**: 是
- **说明**: 授权控制策略
- **可选值**:
  - `0`: 白名单（允许指定来源访问）
  - `1`: 黑名单（拒绝指定来源访问）
- **示例**:
  ```json
  "strategy": 0  // 白名单策略
  ```

## 规则示例解析

### 示例1：白名单控制
```json
{
  "resource": "authority-control",
  "limitApp": "white_list",
  "strategy": 0
}
```
**解释**: 
- 对 `authority-control` 资源启用白名单控制
- 只有来源应用为 "white_list" 的请求才被允许访问

### 示例2：黑名单控制
```json
{
  "resource": "authority-control",
  "limitApp": "black_list",
  "strategy": 1
}
```
**解释**: 
- 对 `authority-control` 资源启用黑名单控制
- 来源应用为 "black_list" 的请求将被拒绝访问

## 授权控制机制

### 1. 白名单策略（strategy=0）
- **工作原理**: 只允许指定的来源应用访问资源
- **配置方式**: 
  ```json
  {
    "resource": "test-resource",
    "limitApp": "trusted_app",
    "strategy": 0
  }
  ```
- **效果**: 只有来源为 "trusted_app" 的请求可以访问 "test-resource"

### 2. 黑名单策略（strategy=1）
- **工作原理**: 拒绝指定的来源应用访问资源
- **配置方式**: 
  ```json
  {
    "resource": "test-resource",
    "limitApp": "blocked_app",
    "strategy": 1
  }
  ```
- **效果**: 来源为 "blocked_app" 的请求将被拒绝访问 "test-resource"

## 实际应用场景

### 1. 基于来源应用的访问控制
```json
[
  {
    "resource": "admin-api",
    "limitApp": "admin-service",
    "strategy": 0
  }
]
```
**说明**: 只允许 "admin-service" 应用访问 "admin-api" 资源

### 2. IP地址黑白名单控制
通过自定义请求来源解析器，可以实现基于 IP 的访问控制：
```json
[
  {
    "resource": "sensitive-api",
    "limitApp": "192.168.1.100",
    "strategy": 1
  }
]
```
**说明**: 拒绝来自 "192.168.1.100" IP 的访问

### 3. 用户角色访问控制
```json
[
  {
    "resource": "user-profile",
    "limitApp": "admin,vip-user",
    "strategy": 0
  }
]
```
**说明**: 只允许 "admin" 和 "vip-user" 角色访问用户资料

## 配置实现方式

### 1. 自定义请求来源解析器
需要实现 `RequestOriginParser` 接口来解析请求来源：

```java
@Component
public class CustomRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从请求头中获取来源标识
        String origin = request.getHeader("X-App-Origin");
        return StringUtils.isEmpty(origin) ? "default" : origin;
    }
}
```

### 2. 基于请求头的控制
```java
@Component
public class HeaderOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 根据不同的请求头确定来源
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        
        if (userAgent != null && userAgent.contains("mobile")) {
            return "mobile-app";
        } else if (referer != null && referer.contains("admin")) {
            return "admin-web";
        }
        return "default";
    }
}
```

## 最佳实践

### 1. 明确控制目标
- 确定需要保护的资源
- 明确允许或拒绝的来源

### 2. 合理使用策略
- 白名单：适用于访问来源明确且有限的场景
- 黑名单：适用于需要排除特定来源的场景

### 3. 动态配置管理
- 通过 Nacos 等配置中心实现动态更新
- 避免硬编码来源应用名称

## 注意事项

1. **授权规则需要配合自定义的 RequestOriginParser 实现**
2. **limitApp 中的值需要与 RequestOriginParser 返回的值对应**
3. **白名单策略下，未在名单中的来源将被拒绝**
4. **黑名单策略下，名单中的来源将被拒绝，其他来源允许访问**
5. **授权规则的优先级高于普通的流控规则**
6. **一个资源可以同时配置多个授权规则**