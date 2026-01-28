/**
 * 简化版配置 - 用于减少 HTML 中的重复代码
 * 这个文件提供了一些辅助函数和配置,但不改变现有架构
 */

// Tab 配置
export const tabs = [
    { key: 'control', label: '控制面' },
    { key: 'governance', label: '治理面' },
    { key: 'communication', label: '通信层' },
    { key: 'gateway', label: '网关与调度' },
    { key: 'data', label: '数据与运维' }
];

// 分类标题映射
export const categoryTitles = {
    control: '控制面',
    governance: '治理面 - 流量控制与容错保护',
    communication: '通信层',
    gateway: '网关与调度',
    data: '数据与运维'
};

// 最佳实践配置
export const bestPractices = [
    {
        icon: '✅',
        title: '流量防护',
        content: '在网关和服务层使用 Sentinel 进行流量控制，防止级联故障。建议配置 QPS 限流、线程隔离、熔断降级等多个维度的防护。'
    },
    {
        icon: '✅',
        title: '服务通信',
        content: '同步调用使用 OpenFeign + LoadBalancer，异步调用使用消息队列。Dubbo 适合内部高性能调用，REST 适合跨域/外部调用。'
    },
    {
        icon: '✅',
        title: '分布式事务',
        content: '强一致性业务使用 Seata TCC 模式，最终一致性业务使用本地消息表或 SAGA 模式。'
    },
    {
        icon: '✅',
        title: '配置管理',
        content: '敏感配置和环境相关配置通过 Nacos 集中管理，支持应用热更新。避免硬编码和手动修改。'
    },
    {
        icon: '✅',
        title: '可观测性',
        content: '使用 OpenTelemetry 统一采集 Trace、Metric、Log，提升故障排查效率。在链路上加上业务标记便于问题追踪。'
    },
    {
        icon: '✅',
        title: '网关安全',
        content: '所有流量通过网关，在网关层做统一的鉴权、限流、日志记录。使用黑白名单和 JWT Token 控制访问权限。'
    }
];

// 代码示例配置
export const codeExamples = {
    yaml: {
        timeout: `spring:
  cloud:
    openfeign:
      client:
        config:
          # 默认配置
          default:
            logger-level: full
            connect-timeout: 1000
            read-timeout: 2000
          # 具体 feign 客户端的超时配置
          service-product:
            logger-level: full
            connect-timeout: 3000
            read-timeout: 4000`
    },
    java: {
        retryer: `@Configuration
public class ProductServiceConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }
}`
    }
};
