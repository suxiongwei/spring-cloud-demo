/**
 * 微服务配置数据
 */
export const servicesConfig = {
    // 控制面
    control: [
        {
            id: 'nacos',
            title: 'Nacos',
            subtitle: '配置管理、服务发现、服务管理',
            description: '一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台',
            logo: 'images/logos/nacos.png',
            github: 'https://github.com/alibaba/nacos',
            website: 'https://nacos.io'
        },
        {
            id: 'opensergo',
            title: 'OpenSergo',
            subtitle: '治理标准',
            description: '开放通用的微服务治理标准,覆盖流量治理、服务容错等。',
            implementationStatus: 'planned',
            roadmap: {
                milestone: '2026-Q2',
                minimalDeliverable: '提供统一治理规则模型展示与规则下发示例',
                prerequisites: ['scenario-catalog', 'contract-validator']
            },
            logo: 'images/logos/opensergo.png',
            github: 'https://github.com/opensergo/opensergo-specification',
            website: 'https://opensergo.io'
        }
    ],

    // 治理面
    governance: [
        {
            id: 'sentinel',
            title: 'Sentinel',
            subtitle: '流量防卫兵',
            description: '面向分布式服务架构的流量控制组件,提供流量控制、熔断降级、系统负载保护等功能',
            logo: 'images/logos/sentinel.png',
            github: 'https://github.com/alibaba/Sentinel',
            website: 'https://sentinelguard.io'
        }
    ],

    // 通信层
    communication: [
        {
            id: 'dubbo',
            title: 'Dubbo',
            subtitle: '高性能 RPC 框架',
            description: 'Apache Dubbo 是一款高性能、轻量级的开源 Java RPC 框架',
            logo: 'images/logos/dubbo.png',
            github: 'https://github.com/apache/dubbo',
            website: 'https://dubbo.apache.org'
        },
        {
            id: 'rocketmq',
            title: 'RocketMQ',
            subtitle: '消息队列',
            description: '分布式消息中间件,支持事务消息、顺序消息、定时消息等',
            implementationStatus: 'implemented',
            roadmap: {
                milestone: '2026-Q1',
                minimalDeliverable: '订单业务链路 10 场景可演练',
                prerequisites: ['demo endpoint contract', 'scenario cards']
            },
            logo: 'images/logos/rocketmq.png',
            github: 'https://github.com/apache/rocketmq',
            website: 'https://rocketmq.apache.org'
        },
        {
            id: 'sca',
            title: 'Spring Cloud',
            subtitle: 'REST 调用',
            description: 'Spring Cloud Alibaba 提供的微服务开发一站式解决方案',
            logo: 'images/logos/spring-cloud.png',
            github: 'https://github.com/alibaba/spring-cloud-alibaba',
            website: 'https://spring.io/projects/spring-cloud-alibaba'
        }
    ],

    // 网关与调度
    gateway: [
        {
            id: 'higress',
            title: 'Higress',
            subtitle: '云原生网关',
            description: '基于 Envoy 的云原生 API 网关,提供流量管理、安全防护等能力',
            implementationStatus: 'demo-only',
            logo: 'images/logos/higress.png',
            github: 'https://github.com/alibaba/higress',
            website: 'https://higress.io'
        },
        {
            id: 'schedulerx',
            title: 'SchedulerX',
            subtitle: '分布式任务调度',
            description: '阿里巴巴自研的分布式任务调度平台',
            implementationStatus: 'planned',
            roadmap: {
                milestone: '2026-Q3',
                minimalDeliverable: '完成单任务触发、分片执行与结果回执演示',
                prerequisites: ['scheduler console', 'task execution service']
            },
            logo: 'images/logos/schedulerx.png',
            github: 'https://github.com/alibaba/SchedulerX',
            website: 'https://www.aliyun.com/aliware/schedulerx'
        }
    ],

    // 数据与运维
    data: [
        {
            id: 'seata',
            title: 'Seata',
            subtitle: '分布式事务',
            description: '简单易用的分布式事务解决方案,支持 AT、TCC、SAGA、XA 模式',
            logo: 'images/logos/seata.png',
            github: 'https://github.com/seata/seata',
            website: 'https://seata.io'
        },
        {
            id: 'opentelemetry',
            title: 'OpenTelemetry',
            subtitle: '可观测性',
            description: '云原生可观测性框架,统一采集 Trace、Metric、Log',
            implementationStatus: 'demo-only',
            logo: 'images/logos/opentelemetry.png',
            github: 'https://github.com/open-telemetry',
            website: 'https://opentelemetry.io'
        }
    ]
};

/**
 * 测试用例配置
 */
export const testCasesConfig = {
    sentinel: [
        {
            id: 'qps',
            label: 'QPS 限流',
            description: '限制每秒最多处理的请求数量',
            endpoint: '/api/order/rateLimit/qps',
            inputFields: [
                { key: 'qpsTimes', type: 'number', placeholder: '次数', default: 10 }
            ],
            buttons: [
                { label: '测试 QPS 限流', action: 'trigger', params: ['qps', 'qpsTimes'] }
            ]
        },
        {
            id: 'thread',
            label: '线程隔离',
            description: '限制并发执行的线程数量',
            inputFields: [
                { key: 'threadTimes', type: 'number', placeholder: '并发数', default: 5 }
            ],
            buttons: [
                { label: '测试线程隔离', action: 'testThread' }
            ]
        }
    ],

    nacos: [
        {
            id: 'services',
            label: '服务发现',
            description: '查询 Nacos 中注册的所有服务实例',
            endpoint: '/api/order/demo/nacos/services',
            buttons: [
                { label: '查询服务列表', action: 'testNacosServices' }
            ]
        },
        {
            id: 'config',
            label: '配置管理',
            description: '加载 Nacos 的配置,动态刷新',
            endpoint: '/api/order/demo/nacos-config',
            buttons: [
                { label: '读取动态配置', action: 'testNacosConfig' }
            ]
        }
    ],

    dubbo: [
        {
            id: 'sync',
            label: 'RPC 调用',
            description: '使用 Dubbo 进行远程调用',
            endpoint: '/api/order/dubbo/call-sync',
            inputFields: [
                { key: 'dubboProductId', type: 'number', placeholder: 'ID', default: 1 }
            ],
            buttons: [
                { label: '同步调用', action: 'testDubboSync' },
                { label: '批量调用', action: 'testDubboBatch' },
                { label: '查询所有', action: 'testDubboListAll' }
            ]
        }
    ],

    sca: [
        {
            id: 'feign-enhanced',
            label: 'OpenFeign REST 调用',
            description: '展示 OpenFeign 的完整功能',
            endpoint: '/api/order/demo/feign/call-enhanced',
            inputFields: [
                { key: 'feignProductId', type: 'number', placeholder: '商品ID', default: 1 }
            ],
            buttons: [
                { label: '正常调用', action: 'testFeignEnhanced', params: ['normal'] },
                { label: '模拟失败', action: 'testFeignEnhanced', params: ['error'], variant: 'danger' },
                { label: '模拟超时降级', action: 'testFeignEnhanced', params: ['degrade'], variant: 'warning' }
            ]
        },
        {
            id: 'load-balance',
            label: 'LoadBalancer 负载均衡',
            description: 'Spring Cloud LoadBalancer 客户端负载均衡',
            endpoint: '/api/order/demo/load-balance',
            buttons: [
                { label: '测试负载均衡策略', action: 'testLoadBalance' }
            ]
        }
    ],

    seata: [
        {
            id: 'tcc',
            label: 'TCC 事务模式',
            description: 'TCC 保证强一致性',
            inputFields: [
                { key: 'tccCommodityCode', type: 'text', placeholder: '商品编码', default: 'P0001' },
                { key: 'tccCount', type: 'number', placeholder: '数量', default: 2 }
            ],
            buttons: [
                { label: '提交事务', action: 'callWithResultDisplay', params: ['endpoint("tcc-ok")', 'tcc-ok', 'seata-tcc-ok'] },
                { label: '回滚事务', action: 'callWithResultDisplay', params: ['endpoint("tcc-fail")', 'tcc-fail', 'seata-tcc-fail'], variant: 'danger' }
            ]
        }
    ],

    higress: [
        {
            id: 'routing',
            label: '网关路由',
            description: '检查网关路由规则配置',
            buttons: [
                { label: '检查路由规则', action: 'testGatewayRouting' }
            ]
        }
    ],

    opentelemetry: [
        {
            id: 'tracing',
            label: '分布式链路 Trace',
            description: '生成模拟的应用链路数据',
            buttons: [
                { label: '生成模拟链路数据', action: 'testTracing' }
            ]
        }
    ]
};
