import AppHeader from './components/AppHeader.js'
import ResultDisplay from './components/ResultDisplay.js'

const { createApp, ref, reactive } = Vue

const app = createApp({
    components: {
        AppHeader,
        ResultDisplay
    },
    data() {
        return {
            activePanoramaTab: localStorage.getItem('service-demo-tab') || 'communication',
            activeComponent: localStorage.getItem('service-demo-component') || 'dubbo',
            activeSection: localStorage.getItem('service-demo-section') || null,
            coreStageIds: ['sentinel', 'nacos', 'gateway', 'sca', 'dubbo', 'redis', 'seata', 'rocketmq'],
            roadmapStageIds: ['higress', 'opentelemetry', 'k8s', 'opensergo', 'chaosblade', 'appactive', 'schedulerx', 'arctic'],
            showRoadmapSection: localStorage.getItem('service-demo-roadmap-expanded') === 'true',
            // 每个组件的默认测试场景
            defaultSections: {
                sentinel: 'sentinel-qps',
                nacos: 'nacos-services',
                dubbo: 'dubbo-batch',
                rocketmq: 'rocketmq-business-chain',
                redis: 'redis-data-structures'
            },
            componentData: {
                sentinel: {
                    title: 'Sentinel',
                    name: 'Sentinel',
                    subtitle: '流量防卫兵',
                    description: '面向分布式服务架构的流量控制组件,提供熔断降级、系统负载保护。',
                    icon: 'sentinel.png',
                    features: [
                        { name: '流量控制', description: '基于 QPS、并发线程数等维度进行流量控制' },
                        { name: '熔断降级', description: '自动检测服务异常并触发熔断保护' },
                        { name: '系统负载保护', description: '保护系统不被过载流量压垮' },
                        { name: '实时监控', description: '提供实时的流量监控和统计功能' }
                    ],
                    actions: ['流量控制测试', '并发控制测试', '热点参数限流', '熔断降级测试'],
                    docs: 'https://sentinelguard.io',
                    github: 'https://github.com/alibaba/Sentinel',
                    website: 'https://sentinelguard.io'
                },
                nacos: {
                    title: 'Nacos',
                    name: 'Nacos',
                    subtitle: '配置管理、服务发现、服务管理',
                    description: '⼀个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台',
                    icon: 'nacos.png',
                    features: [
                        { name: '服务发现', description: '支持 DNS 和 RPC 服务发现' },
                        { name: '配置管理', description: '动态配置管理，支持配置版本管理和回滚' },
                        { name: '健康检查', description: '支持多种健康检查模式' },
                        { name: '命名空间', description: '支持多租户和多环境隔离' }
                    ],
                    actions: ['服务注册发现', '配置管理', '健康检查', '命名空间管理'],
                    docs: 'https://nacos.io',
                    github: 'https://github.com/alibaba/nacos',
                    website: 'https://nacos.io'
                },
                dubbo: {
                    title: 'Dubbo',
                    name: 'Dubbo',
                    subtitle: 'RPC 框架',
                    description: '高性能、轻量级的开源 Java RPC 框架,提供面向接口的远程方法调用。',
                    icon: 'dubbo.png',
                    features: [
                        { name: '远程调用', description: '基于接口的高性能 RPC 调用' },
                        { name: '负载均衡', description: '支持多种负载均衡策略' },
                        { name: '服务治理', description: '提供完整的服务治理能力' },
                        { name: '多协议支持', description: '支持 Dubbo、REST、gRPC 等协议' }
                    ],
                    actions: ['同步调用', '异步调用', '批量调用', '服务列表查询'],
                    docs: 'https://dubbo.apache.org',
                    github: 'https://github.com/apache/dubbo',
                    website: 'https://dubbo.apache.org'
                },
                seata: {
                    title: 'Seata',
                    name: 'Seata',
                    subtitle: '分布式事务',
                    description: '致力于提供高性能和简单易用的分布式事务服务,支持 AT、TCC、SAGA 等模式。',
                    icon: 'seata.png',
                    features: [
                        { name: 'AT 模式', description: '两阶段提交，无侵入式事务控制' },
                        { name: 'TCC 模式', description: 'Try-Confirm-Cancel 三阶段事务' },
                        { name: 'SAGA 模式', description: '长事务解决方案' },
                        { name: 'XA 模式', description: '标准 XA 协议支持' }
                    ],
                    actions: ['TCC 提交测试', 'TCC 回滚测试', '事务查询', '分布式事务示例'],
                    docs: 'https://seata.io',
                    github: 'https://github.com/seata/seata',
                    website: 'https://seata.io'
                },
                higress: {
                    title: 'Higress',
                    name: 'Higress',
                    subtitle: '云原生网关',
                    description: '基于 Envoy 的云原生网关,实现了流量网关、微服务网关、安全网关三合一。',
                    implementationStatus: 'demo-only',
                    icon: 'Higress.png',
                    features: [
                        { name: '流量网关', description: '统一的流量入口管理' },
                        { name: '微服务网关', description: '服务路由和负载均衡' },
                        { name: '安全网关', description: '认证授权和安全防护' },
                        { name: '插件生态', description: '丰富的插件扩展能力' }
                    ],
                    actions: ['路由配置', '认证测试', '流量控制', '插件管理'],
                    docs: 'https://higress.io',
                    github: 'https://github.com/alibaba/higress',
                    website: 'https://higress.io'
                },
                gateway: {
                    title: 'Spring Cloud Gateway',
                    name: 'Spring Cloud Gateway',
                    subtitle: 'API 网关',
                    description: '基于 Spring 5.0、Spring Boot 2.0 和 Project Reactor 等技术开发的 API 网关，提供统一的路由转发和过滤器机制。',
                    icon: 'spring-cloud.png',
                    features: [
                        { name: '动态路由', description: '基于 Predicate 匹配的动态路由配置' },
                        { name: '过滤器', description: '前置和后置过滤器，支持请求修改' },
                        { name: '负载均衡', description: '集成 Ribbon 实现客户端负载均衡' },
                        { name: '限流熔断', description: '集成 Sentinel 实现限流和熔断' }
                    ],
                    actions: ['路由测试', '认证测试', '限流测试', '过滤器测试'],
                    docs: 'https://spring.io/projects/spring-cloud-gateway',
                    github: 'https://github.com/spring-cloud/spring-cloud-gateway',
                    website: 'https://spring.io/projects/spring-cloud-gateway'
                },
                sca: {
                    title: 'Spring Cloud',
                    name: 'Spring Cloud',
                    subtitle: '微服务标准',
                    description: '整合 OpenFeign、LoadBalancer 等标准组件,提供一站式解决方案。',
                    icon: 'sca.svg',
                    features: [
                        { name: '服务注册发现', description: '基于 Nacos 的服务注册与发现' },
                        { name: '声明式调用', description: 'OpenFeign 声明式 HTTP 客户端' },
                        { name: '负载均衡', description: 'Spring Cloud LoadBalancer' },
                        { name: '配置中心', description: 'Nacos Config 配置管理' }
                    ],
                    actions: ['Feign 调用测试', '负载均衡测试', '配置刷新', '健康检查'],
                    docs: 'https://spring.io/projects/spring-cloud-alibaba',
                    github: 'https://github.com/alibaba/spring-cloud-alibaba',
                    website: 'https://spring.io/projects/spring-cloud-alibaba'
                },
                opensergo: {
                    title: 'OpenSergo',
                    name: 'OpenSergo',
                    subtitle: '治理标准',
                    description: '开放通用的微服务治理标准,覆盖流量治理、服务容错等。',
                    implementationStatus: 'planned',
                    roadmap: {
                        milestone: '2026-Q2',
                        minimalDeliverable: '统一治理规则定义与示例下发链路',
                        prerequisites: ['catalog status model', 'rule source integration']
                    },
                    icon: 'opensergo.png',
                    features: [
                        { name: '流量治理', description: '统一的流量治理标准' },
                        { name: '服务容错', description: '标准化的容错机制' },
                        { name: '治理规范', description: '跨语言的治理规范' },
                        { name: '生态集成', description: '与主流框架无缝集成' }
                    ],
                    actions: ['流量规则配置', '容错策略配置', '治理规则查询', '生态集成测试'],
                    docs: 'https://opensergo.io',
                    github: 'https://github.com/opensergo/opensergo-specification',
                    website: 'https://opensergo.io'
                },
                chaosblade: {
                    title: 'ChaosBlade',
                    name: 'ChaosBlade',
                    subtitle: '混沌工程',
                    description: '故障注入、混沌实验、系统韧性验证工具。',
                    implementationStatus: 'planned',
                    roadmap: {
                        milestone: '2026-Q3',
                        minimalDeliverable: 'CPU/网络故障注入最小实验脚本',
                        prerequisites: ['safe sandbox', 'rollback playbook']
                    },
                    icon: 'ChaosBlade.png',
                    features: [
                        { name: '故障注入', description: '支持多种故障场景模拟' },
                        { name: '混沌实验', description: '自动化混沌实验执行' },
                        { name: '韧性验证', description: '系统韧性评估和验证' },
                        { name: '多平台支持', description: '支持 Kubernetes、Docker 等平台' }
                    ],
                    actions: ['CPU 故障注入', '内存故障注入', '网络故障注入', '磁盘故障注入'],
                    docs: 'https://chaosblade.io',
                    github: 'https://github.com/chaosblade-io/chaosblade',
                    website: 'https://chaosblade.io'
                },
                appactive: {
                    title: 'AppActive',
                    name: 'AppActive',
                    subtitle: '多活容灾',
                    description: '应用多活架构、异地多活、容灾切换解决方案。',
                    implementationStatus: 'planned',
                    roadmap: {
                        milestone: '2026-Q3',
                        minimalDeliverable: '单业务链路的多活路由模拟',
                        prerequisites: ['traffic tagging', 'region metadata']
                    },
                    icon: 'appactive.svg',
                    features: [
                        { name: '应用多活', description: '跨地域的应用多活架构' },
                        { name: '异地容灾', description: '异地容灾和故障切换' },
                        { name: '流量路由', description: '智能流量路由和调度' },
                        { name: '数据同步', description: '跨地域数据同步机制' }
                    ],
                    actions: ['多活配置', '容灾切换', '流量路由测试', '数据同步验证'],
                    docs: 'https://doc.appactive.io',
                    github: 'https://github.com/alibaba/AppActive',
                    website: 'https://doc.appactive.io'
                },
                rocketmq: {
                    title: 'RocketMQ',
                    name: 'RocketMQ',
                    subtitle: '消息队列',
                    description: '异步消息通信、削峰填谷、事件驱动、流式处理。',
                    implementationStatus: 'implemented',
                    roadmap: {
                        milestone: '2026-Q1',
                        minimalDeliverable: '订单链路 10 个企业场景可直接演练',
                        prerequisites: ['demo endpoint contract', 'scenario result viewer']
                    },
                    icon: 'rocketmq.svg',
                    features: [
                        { name: '消息队列', description: '高可靠的消息传递' },
                        { name: '削峰填谷', description: '流量削峰和缓冲' },
                        { name: '事件驱动', description: '事件驱动架构支持' },
                        { name: '流式处理', description: '流式数据处理能力' }
                    ],
                    actions: ['消息发送', '消息消费', '消息查询', '事务消息测试'],
                    docs: 'https://rocketmq.apache.org',
                    github: 'https://github.com/apache/rocketmq',
                    website: 'https://rocketmq.apache.org'
                },
                schedulerx: {
                    title: 'SchedulerX',
                    name: 'SchedulerX',
                    subtitle: '任务调度',
                    description: '分布式任务调度、定时任务、工作流调度、分布式计算。',
                    implementationStatus: 'planned',
                    roadmap: {
                        milestone: '2026-Q3',
                        minimalDeliverable: '定时任务触发与执行日志回显',
                        prerequisites: ['scheduler instance', 'task runner']
                    },
                    icon: 'schedulerx.svg',
                    features: [
                        { name: '分布式调度', description: '分布式任务调度和执行' },
                        { name: '定时任务', description: 'Cron 表达式定时任务' },
                        { name: '工作流调度', description: '复杂工作流编排' },
                        { name: '分布式计算', description: '分布式计算框架支持' }
                    ],
                    actions: ['定时任务配置', '工作流编排', '任务执行', '任务监控'],
                    docs: 'https://www.aliyun.com/aliware/schedulerx',
                    github: 'https://github.com/alibaba/SchedulerX',
                    website: 'https://www.aliyun.com/aliware/schedulerx'
                },
                k8s: {
                    title: 'Kubernetes',
                    name: 'Kubernetes',
                    subtitle: '容器编排',
                    description: '自动化容器的部署、扩展和管理,云原生应用的基础设施。',
                    icon: 'k8s.svg',
                    features: [
                        { name: '容器编排', description: '自动化容器部署和管理' },
                        { name: '服务发现', description: '内置服务发现和负载均衡' },
                        { name: '自动扩缩容', description: '基于负载自动扩缩容' },
                        { name: '滚动更新', description: '零停机滚动更新' }
                    ],
                    actions: ['Pod 管理', '服务配置', '部署应用', '扩缩容操作'],
                    docs: 'https://kubernetes.io',
                    github: 'https://github.com/kubernetes/kubernetes',
                    website: 'https://kubernetes.io'
                },
                opentelemetry: {
                    title: 'OpenTelemetry',
                    name: 'OpenTelemetry',
                    subtitle: '可观测性',
                    description: '云原生可观测性标准,提供 Trace、Metric、Log 的统一采集。',
                    implementationStatus: 'demo-only',
                    icon: 'opentelemetry.svg',
                    features: [
                        { name: '链路追踪', description: '分布式链路追踪' },
                        { name: '指标采集', description: '应用和系统指标采集' },
                        { name: '日志收集', description: '统一日志收集和分析' },
                        { name: '多语言支持', description: '支持多种编程语言' }
                    ],
                    actions: ['链路追踪测试', '指标监控', '日志查询', '可观测性配置'],
                    docs: 'https://opentelemetry.io',
                    github: 'https://github.com/open-telemetry',
                    website: 'https://opentelemetry.io'
                },
                redis: {
                    title: 'Redis',
                    name: 'Redis',
                    subtitle: '内存数据库',
                    description: '开源的内存数据结构存储,可用作数据库、缓存和消息中间件。',
                    icon: 'Redis.svg',
                    features: [
                        { name: '高性能', description: '基于内存操作,读写速度极快' },
                        { name: '数据结构', description: '支持 String、Hash、List、Set 等多种数据结构' },
                        { name: '持久化', description: '支持 RDB 和 AOF 两种持久化方式' },
                        { name: '主从复制', description: '支持主从复制和哨兵模式' }
                    ],
                    actions: ['基础数据结构', '分布式锁', '缓存问题', '限流计数', '持久化监控'],
                    docs: 'https://redis.io',
                    github: 'https://github.com/redis/redis',
                    website: 'https://redis.io'
                }
            },

            // Test Parameters
            qpsTimes: 10,
            threadTimes: 5,
            hotUserId: 'user-001',
            hotTimes: 10,
            degradeConcurrent: 5,
            tccCommodityCode: 'P0001',
            tccCount: 2,
            feignProductId: 1,
            dubboProductId: 1,
            dubboClientRegion: 'hangzhou',
            dubboConcurrentCount: 10,
            dubboSleepTime: 3000,
            dubboConcurrencyType: 'executes',
            dubboActiveCount: 5,
            dubboRequestCount: 20,
            dubboFilterMessage: 'Hello Dubbo Filter',
            dubboVersionGroupName: 'World',
            dubboVersionGroupType: 'v1-default',
            compareTimes: 5,
            loadBalanceRequestCount: 20,
            loadBalanceStrategy: 'random',
            loadBalanceParam: 1,
            loadBalanceResult: null,

            // 负载均衡策略详情弹窗
            showStrategyModal: false,
            currentStrategyDetails: null,

            // System
            history: [],
            showDetailModal: false,
            detailView: '',
            comparisonData: [],
            
            // 主题切换
            isDarkMode: localStorage.getItem('theme-mode') === 'dark' || 
                        (!localStorage.getItem('theme-mode') && window.matchMedia('(prefers-color-scheme: dark)').matches),

            // 下拉菜单状态
            openDropdown: null,

            // Individual result displays for each test scenario
            resultDisplays: {},
            // Loading states for each test
            loadingStates: {},

            // 二级菜单展开状态
            expandedMenus: {
                dubbo: false,
                sentinel: false,
                nacos: false,
                rocketmq: false,
                redis: false
            },

            // 回到顶部按钮状态
            showBackToTop: false
        }
    },
    watch: {
        activePanoramaTab(val) { localStorage.setItem('service-demo-tab', val) },
        activeComponent(val) { 
            localStorage.setItem('service-demo-component', val)
            // 设置为该组件的默认测试场景
            this.activeSection = this.defaultSections[val] || null
            localStorage.setItem('service-demo-section', this.activeSection || '')
            // 自动展开对应的二级菜单
            if (this.expandedMenus.hasOwnProperty(val)) {
                this.expandedMenus[val] = true
            }
        },
        activeSection(val) { 
            localStorage.setItem('service-demo-section', val || '')
        },
        showDetailModal(val) {
            if (val) {
                document.body.style.overflow = 'hidden'
            } else {
                document.body.style.overflow = ''
            }
        }
    },
    methods: {
        scrollToSection(sectionId) {
            const element = document.getElementById(sectionId)
            if (element) {
                const elementPosition = element.getBoundingClientRect().top
                const offsetPosition = elementPosition + window.scrollY - 120
                
                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                })
                this.activeSection = sectionId
            }
        },
        scrollToTop() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            })
        },
        handleScroll() {
            const componentSections = {
                dubbo: [
                    'dubbo-batch',
                    'dubbo-list-all',
                    'dubbo-timeout',
                    'dubbo-exception',
                    'dubbo-async',
                    'dubbo-region',
                    'dubbo-concurrency',
                    'dubbo-loadbalance',
                    'dubbo-filter',
                    'dubbo-version-group',
                    'protocol-compare'
                ],
                sentinel: [
                    'sentinel-qps',
                    'sentinel-thread',
                    'sentinel-hot'
                ],
                nacos: [
                    'nacos-services',
                    'nacos-config'
                ],
                seata: [
                    'seata-tcc'
                ],
                higress: [
                    'higress-routing',
                    'higress-auth'
                ],
                sca: [
                    'sca-feign',
                    'sca-loadbalance',
                    'sca-timeout',
                    'sca-interceptor',
                    'sca-config',
                    'sca-health'
                ],
                opentelemetry: [
                    'opentelemetry-tracing'
                ],
                redis: [
                    'redis-data-structures',
                    'redis-distributed-lock',
                    'redis-cache-issues',
                    'redis-rate-limit',
                    'redis-pipeline',
                    'redis-transaction',
                    'redis-pubsub',
                    'redis-persistence'
                ],
                rocketmq: [
                    'rocketmq-business-chain'
                ]
            }

            const sections = componentSections[this.activeComponent] || []
            let currentSection = null
            const scrollPosition = window.scrollY + 100

            for (const sectionId of sections) {
                const element = document.getElementById(sectionId)
                if (element) {
                    const rect = element.getBoundingClientRect()
                    const elementTop = rect.top + window.scrollY
                    const elementBottom = elementTop + rect.height

                    if (scrollPosition >= elementTop && scrollPosition < elementBottom) {
                        currentSection = sectionId
                        break
                    }
                }
            }

            if (currentSection) {
                this.activeSection = currentSection
            }

            this.showBackToTop = window.scrollY > 100
        },
        selectComponent(id) {
            if (this.isRoadmapComponent(id) && !this.showRoadmapSection) {
                return
            }
            this.activeComponent = id
            this.activeSection = null
            this.updateActivePanoramaTab(id)
            // 自动展开对应的二级菜单
            if (this.expandedMenus.hasOwnProperty(id)) {
                this.expandedMenus[id] = true
            }
        },
        isCoreComponent(componentId) {
            return this.coreStageIds.includes(componentId)
        },
        isRoadmapComponent(componentId) {
            return this.roadmapStageIds.includes(componentId)
        },
        toggleRoadmapSection() {
            this.showRoadmapSection = !this.showRoadmapSection
            localStorage.setItem('service-demo-roadmap-expanded', String(this.showRoadmapSection))
        },
        toggleComponentMenu(componentId) {
            this.expandedMenus[componentId] = !this.expandedMenus[componentId]
        },
        selectSection(componentId, sectionId) {
            this.activeComponent = componentId
            this.activeSection = sectionId
            this.expandedMenus[componentId] = true
            this.updateActivePanoramaTab(componentId)
            this.scrollToSection(sectionId)
        },
        updateActivePanoramaTab(componentId) {
            const componentMap = {
                'nacos': 'control',
                'opensergo': 'control',
                'sentinel': 'governance',
                'chaosblade': 'governance',
                'appactive': 'governance',
                'dubbo': 'communication',
                'sca': 'communication',
                'rocketmq': 'communication',
                'seata': 'communication',
                'higress': 'gateway',
                'schedulerx': 'gateway',
                'gateway': 'gateway',
                'k8s': 'data',
                'opentelemetry': 'data',
                'arctic': 'data',
                'redis': 'data'
            }
            const menuKey = componentMap[componentId] || 'communication'
            this.activePanoramaTab = menuKey
        },
        toggleDropdown(index) {
            if (this.openDropdown === index) {
                this.openDropdown = null
            } else {
                this.openDropdown = index
            }
        },
        closeDropdown() {
            this.openDropdown = null
        },
        formatTime(date) {
            return date.toLocaleTimeString('zh-CN', { hour12: false }) + '.' +
                date.getMilliseconds().toString().padStart(3, '0')
        },
        endpoint(t) {
            const map = {
                'qps': '/api/order/rateLimit/qps',
                'thread': '/api/order/rateLimit/thread',
                'hot': '/api/order/hotspot/param?userId=1001&productId=2002',
                'degrade': '/api/order/degrade/rt',
                'tcc-ok': `/api/business/purchase/tcc/verify?userId=U1001&commodityCode=${this.tccCommodityCode}&count=${this.tccCount}&fail=false`,
                'tcc-fail': `/api/business/purchase/tcc/verify?userId=U1001&commodityCode=${this.tccCommodityCode}&count=${this.tccCount}&fail=true`,
                'feign': `/api/order/demo/feign/call-enhanced?productId=${this.feignProductId}`,
                'dubbo-sync': `/api/order/dubbo/call-sync?productId=${this.dubboProductId}`,
                'dubbo-batch': '/api/order/dubbo/call-batch',
                'dubbo-list-all': '/api/order/dubbo/list-all',
                'dubbo-timeout': `/api/order/dubbo/call-timeout?productId=${this.dubboProductId}&sleepTime=4000`,
                'dubbo-exception': `/api/order/dubbo/call-exception?productId=${this.dubboProductId}`,
                'dubbo-async': `/api/order/dubbo/call-async?productId=${this.dubboProductId}`,
                'dubbo-region': `/api/order/dubbo/call-region?productId=${this.dubboProductId}&clientRegion=${this.dubboClientRegion}`,
                'dubbo-concurrency': `/api/order/dubbo/concurrency?concurrentCount=${this.dubboConcurrentCount}&sleepTime=${this.dubboSleepTime}`,
                'dubbo-actives': `/api/order/dubbo/actives-test?activeCount=${this.dubboActiveCount}&requestCount=${this.dubboRequestCount}`,
                'dubbo-leastactive': `/api/order/dubbo/leastactive-test`,
                'dubbo-filter': `/api/order/dubbo/filter-test?message=${this.dubboFilterMessage}`,
                'dubbo-version-group-v1-default': `/api/order/dubbo/version-group/v1-default?name=${this.dubboVersionGroupName}`,
                'dubbo-version-group-v2-default': `/api/order/dubbo/version-group/v2-default?name=${this.dubboVersionGroupName}`,
                'dubbo-version-group-v1-groupA': `/api/order/dubbo/version-group/v1-groupA?name=${this.dubboVersionGroupName}`,
                'dubbo-version-group-v1-groupB': `/api/order/dubbo/version-group/v1-groupB?name=${this.dubboVersionGroupName}`,
                'dubbo-version-group-v2-groupA': `/api/order/dubbo/version-group/v2-groupA?name=${this.dubboVersionGroupName}`,
                'dubbo-version-group-compare': `/api/order/dubbo/version-group/compare?name=${this.dubboVersionGroupName}`,
                'compare-feign': `/api/order/demo/feign/call-enhanced?productId=1`,
                'compare-dubbo': `/api/order/dubbo/call-sync?productId=1`,
                'nacos-services': '/api/order/demo/nacos/services',
                'nacos-config': '/api/order/demo/nacos-config',
                'load-balance': '/api/order/demo/load-balance',
                'tracing': '/api/order/demo/tracing',
                'gateway-routing': '/api/order/demo/gateway-routing',
                'gateway-auth-pass': '/api/order/config?auth-test=pass',
                'gateway-auth-fail': '/api/order/config?auth-test=reject',
                'async': '/api/order/demo/async-parallel',
                'circuit-breaker': '/api/order/degrade/rt',
                'timeout-retry': '/api/order/demo/timeout-retry',
                'feign-interceptor': '/api/order/demo/feign-interceptor',
                'config-refresh': '/api/order/demo/config-refresh',
                'health-check': '/api/order/demo/health-check',
                'feign-enhanced': `/api/order/demo/feign/call-enhanced?productId=${this.feignProductId}`,
                'protocol-compare': '/api/order/dubbo/protocol/compare',
                'protocol-dubbo': '/api/order/dubbo/protocol/dubbo',
                'protocol-triple': '/api/order/dubbo/protocol/triple',
                'protocol-rest': '/api/order/dubbo/protocol/rest',
                'rocketmq/publish-basic': '/api/order/demo/rocketmq/publish-basic',
                'rocketmq/retry': '/api/order/demo/rocketmq/retry',
                'rocketmq/dlq': '/api/order/demo/rocketmq/dlq',
                'rocketmq/idempotent': '/api/order/demo/rocketmq/idempotent',
                'rocketmq/orderly': '/api/order/demo/rocketmq/orderly',
                'rocketmq/delay-close': '/api/order/demo/rocketmq/delay-close',
                'rocketmq/tx/send': '/api/order/demo/rocketmq/tx/send',
                'rocketmq/tx/check': '/api/order/demo/rocketmq/tx/check',
                'rocketmq/tag-filter': '/api/order/demo/rocketmq/tag-filter',
                'rocketmq/replay-dlq': '/api/order/demo/rocketmq/replay-dlq'
            }
            if (map[t]) {
                return map[t]
            }
            if (t.startsWith('redis/')) {
                return `/api/order/dubbo/${t}`
            }
            return '/'
        },
        async trigger(t, times = 1) {
            const url = this.endpoint(t)
            const testId = `sentinel-${t}`

            // Use the new callWithResultDisplay method for individual test tracking
            if (times === 1) {
                await this.callWithResultDisplay(url, t, testId)
            } else {
                // For multiple calls, use the original method but update result display
                const tasks = []
                for (let i = 0; i < times; i++) {
                    tasks.push(this.call(url, t))
                }

                // Set loading state
                this.setResultDisplay(testId, {
                    status: 'loading',
                    message: `正在发送 ${times} 个请求...`,
                    timestamp: this.formatTime(new Date()),
                    endpoint: url
                })

                try {
                    const results = await Promise.all(tasks)
                    // Create a batch result item similar to dev-console format
                    const batchItem = {
                        id: Date.now() + Math.random(),
                        time: this.formatTime(new Date()),
                        type: t,
                        status: '成功',
                        code: 200,
                        msg: `成功发送 ${times} 个请求`,
                        data: { successCount: times, results: results },
                        endpoint: url,
                        rt: '批量'
                    }

                    // Update success state after all calls complete
                    this.setResultDisplay(testId, {
                        status: 'success',
                        message: `成功发送 ${times} 个请求`,
                        timestamp: this.formatTime(new Date()),
                        responseTime: '批量',
                        endpoint: url,
                        rawData: batchItem
                    })
                } catch (error) {
                    // Create error batch result item similar to dev-console format
                    const errorBatchItem = {
                        id: Date.now() + Math.random(),
                        time: this.formatTime(new Date()),
                        type: t,
                        status: '失败',
                        code: 0,
                        msg: `批量请求失败: ${error.message}`,
                        data: null,
                        endpoint: url,
                        rt: 0
                    }

                    this.setResultDisplay(testId, {
                        status: 'error',
                        message: `批量请求失败: ${error.message}`,
                        timestamp: this.formatTime(new Date()),
                        endpoint: url,
                        rawData: errorBatchItem
                    })
                }
            }
        },
        async testThread() {
            const url = this.endpoint('thread')
            const testId = 'sentinel-thread'

            // Set loading state
            this.setResultDisplay(testId, {
                status: 'loading',
                message: `正在执行 ${this.threadTimes} 个并发线程测试...`,
                timestamp: this.formatTime(new Date()),
                endpoint: url
            })

            try {
                const promises = []
                for (let i = 0; i < this.threadTimes; i++) {
                    promises.push(this.call(url, 'thread'))
                    // Small delay to simulate concurrent ramp-up
                    if (i < this.threadTimes - 1) await new Promise(r => setTimeout(r, 10))
                }
                const results = await Promise.all(promises)

                // Create a concurrent result item similar to dev-console format
                const concurrentItem = {
                    id: Date.now() + Math.random(),
                    time: this.formatTime(new Date()),
                    type: 'thread',
                    status: '成功',
                    code: 200,
                    msg: `成功完成 ${this.threadTimes} 个并发线程测试`,
                    data: { successCount: this.threadTimes, results: results },
                    endpoint: url,
                    rt: '并发'
                }

                // Update success state
                this.setResultDisplay(testId, {
                    status: 'success',
                    message: `成功完成 ${this.threadTimes} 个并发线程测试`,
                    timestamp: this.formatTime(new Date()),
                    responseTime: '并发',
                    endpoint: url,
                    rawData: concurrentItem
                })
            } catch (error) {
                // Create error concurrent result item similar to dev-console format
                const errorConcurrentItem = {
                    id: Date.now() + Math.random(),
                    time: this.formatTime(new Date()),
                    type: 'thread',
                    status: '失败',
                    code: 0,
                    msg: `并发测试失败: ${error.message}`,
                    data: null,
                    endpoint: url,
                    rt: 0
                }

                this.setResultDisplay(testId, {
                    status: 'error',
                    message: `并发测试失败: ${error.message}`,
                    timestamp: this.formatTime(new Date()),
                    endpoint: url,
                    rawData: errorConcurrentItem
                })
            }
        },
        async testDegrade() {
            const url = this.endpoint('degrade')
            const tasks = []
            for (let i = 0; i < 5; i++) {
                for (let j = 0; j < this.degradeConcurrent; j++) {
                    tasks.push(this.call(url, 'degrade'))
                }
            }
            await Promise.all(tasks)
        },
        async testNacosServices() { await this.callWithResultDisplay(this.endpoint('nacos-services'), 'nacos-services', 'nacos-services') },
        async testNacosConfig() { await this.callWithResultDisplay(this.endpoint('nacos-config'), 'nacos-config', 'nacos-config') },
        async testDubboSync() { await this.callWithResultDisplay(this.endpoint('dubbo-sync'), 'dubbo-sync', 'dubbo-sync') },
        async testDubboBatch() { await this.callWithResultDisplay(this.endpoint('dubbo-batch'), 'dubbo-batch', 'dubbo-batch') },
        async testDubboListAll() { await this.callWithResultDisplay(this.endpoint('dubbo-list-all'), 'dubbo-list-all', 'dubbo-list-all') },
        async testDubboTimeout() { await this.callWithResultDisplay(this.endpoint('dubbo-timeout'), 'dubbo-timeout', 'dubbo-timeout') },
        async testDubboException() { await this.callWithResultDisplay(this.endpoint('dubbo-exception'), 'dubbo-exception', 'dubbo-exception') },
        async testDubboAsync() { await this.callWithResultDisplay(this.endpoint('dubbo-async'), 'dubbo-async', 'dubbo-async') },
        async testDubboRegion() { await this.callWithResultDisplay(this.endpoint('dubbo-region'), 'dubbo-region', 'dubbo-region') },
        async testDubboConcurrency() { 
            const url = `/api/order/dubbo/concurrency?concurrentCount=${this.dubboConcurrentCount}&sleepTime=${this.dubboSleepTime}&type=${this.dubboConcurrencyType}`;
            
            this.setResultDisplay('dubbo-concurrency', {
                status: 'loading',
                message: `正在通过后端多线程发送 ${this.dubboConcurrentCount} 个并发请求...`,
                timestamp: this.formatTime(new Date()),
                endpoint: url
            });
            
            const startTime = performance.now();
            
            try {
                // 发送单个请求到后端，由后端实现多线程并发
                const response = await axios.get(url);
                const endTime = performance.now();
                
                // 解析后端返回的并发测试结果
                const result = response.data.data;
                
                this.setResultDisplay('dubbo-concurrency', {
                    status: 'success',
                    message: `后端多线程并发测试完成: 成功${result.successCount}个, 失败${result.failCount}个, 限流${result.limitedCount}个`,
                    timestamp: this.formatTime(new Date()),
                    responseTime: Math.round(endTime - startTime),
                    code: response.status,
                    data: result,
                    endpoint: url
                });
                
                // 添加到历史记录
                const item = {
                    id: Date.now() + Math.random(),
                    time: this.formatTime(new Date()),
                    type: 'Dubbo服务端并发控制',
                    status: result.failCount === 0 ? '成功' : '部分成功',
                    code: response.status,
                    msg: `并发数:${this.dubboConcurrentCount}, 成功:${result.successCount}, 失败:${result.failCount}, 限流:${result.limitedCount}`,
                    data: result,
                    endpoint: url,
                    rt: Math.round(endTime - startTime)
                };
                
                this.history.unshift(item);
                if (this.history.length > 100) this.history.pop();
                
            } catch (error) {
                console.error('Dubbo 并发测试失败:', error);
                const endTime = performance.now();
                
                this.setResultDisplay('dubbo-concurrency', {
                    status: 'error',
                    message: `后端多线程并发测试失败: ${error.response?.data?.message || error.message}`,
                    timestamp: this.formatTime(new Date()),
                    responseTime: Math.round(endTime - startTime),
                    code: error.response?.status || 500,
                    endpoint: url
                });
                
                // 添加到历史记录
                const item = {
                    id: Date.now() + Math.random(),
                    time: this.formatTime(new Date()),
                    type: 'Dubbo服务端并发控制',
                    status: '失败',
                    code: error.response?.status || 500,
                    msg: error.response?.data?.message || error.message,
                    endpoint: url,
                    rt: Math.round(endTime - startTime)
                };
                
                this.history.unshift(item);
                if (this.history.length > 100) this.history.pop();
            }
        },
        async testDubboActives() { 
            // Update endpoint with current values
            const url = `/api/order/dubbo/actives-test?activeCount=${this.dubboActiveCount}&requestCount=${this.dubboRequestCount}`;
            
            // 设置加载状态
            this.setResultDisplay('dubbo-actives', {
                status: 'loading',
                message: `正在发送 ${this.dubboRequestCount} 个并发请求(最大并发数: ${this.dubboActiveCount})...`,
                timestamp: this.formatTime(new Date())
            });
            
            // 创建并发请求数组
            const tasks = [];
            const startTime = performance.now();
            
            // 同时发起多个并发请求
            for (let i = 0; i < this.dubboRequestCount; i++) {
                tasks.push(axios.get(url));
            }
            
            try {
                // 等待所有请求完成
                const responses = await Promise.all(tasks);
                const endTime = performance.now();
                const totalTime = endTime - startTime;
                
                // 统计成功和失败的请求
                const successCount = responses.filter(res => res.status === 200).length;
                const failCount = this.dubboRequestCount - successCount;
                
                // 分析响应数据，检查是否有并发限制
                let hasRateLimiting = false;
                let avgResponseTime = 0;
                
                responses.forEach(res => {
                    if (res.data && res.data.message) {
                        // 检查响应中是否包含并发限制相关信息
                        if (res.data.message.includes('并发') || res.data.message.includes('限流') || res.data.message.includes('拒绝')) {
                            hasRateLimiting = true;
                        }
                    }
                });
                
                // 计算平均响应时间
                if (responses.length > 0) {
                    const responseTimes = responses.map(res => res.data && res.data.duration ? 
                        parseInt(res.data.duration) : 0);
                    avgResponseTime = responseTimes.reduce((a, b) => a + b, 0) / responseTimes.length;
                }
                
                // 设置结果
                this.setResultDisplay('dubbo-actives', {
                    status: 'success',
                    message: `消费端并发控制测试完成: 成功 ${successCount}/${this.dubboRequestCount} 个请求，总耗时 ${totalTime.toFixed(2)}ms，平均响应时间 ${avgResponseTime.toFixed(2)}ms`,
                    data: {
                        totalRequests: this.dubboRequestCount,
                        maxConcurrent: this.dubboActiveCount,
                        successCount: successCount,
                        failCount: failCount,
                        totalTime: totalTime.toFixed(2),
                        avgResponseTime: avgResponseTime.toFixed(2),
                        hasRateLimiting: hasRateLimiting,
                        responses: responses.map(res => res.data)
                    },
                    timestamp: this.formatTime(new Date()),
                    endpoint: url
                });
            } catch (error) {
                // 处理错误
                this.setResultDisplay('dubbo-actives', {
                    status: 'error',
                    message: `消费端并发控制测试失败: ${error.message}`,
                    timestamp: this.formatTime(new Date()),
                    endpoint: url
                });
            }
        },
        async testDubboLeastActive() { 
            await this.callWithResultDisplay(this.endpoint('dubbo-leastactive'), 'dubbo-leastactive', 'dubbo-leastactive') 
        },
        async testDubboFilter() {
            const url = `/api/order/dubbo/filter-test?message=${encodeURIComponent(this.dubboFilterMessage)}`;
            await this.callWithResultDisplay(url, 'dubbo-filter', 'dubbo-filter')
        },
        async testDubboVersionGroup() {
            let url;
            switch(this.dubboVersionGroupType) {
                case 'v1-default':
                    url = `/api/order/dubbo/version-group/v1-default?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
                    break;
                case 'v2-default':
                    url = `/api/order/dubbo/version-group/v2-default?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
                    break;
                case 'v1-groupA':
                    url = `/api/order/dubbo/version-group/v1-groupA?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
                    break;
                case 'v1-groupB':
                    url = `/api/order/dubbo/version-group/v1-groupB?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
                    break;
                case 'v2-groupA':
                    url = `/api/order/dubbo/version-group/v2-groupA?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
                    break;
                case 'compare':
                    url = `/api/order/dubbo/version-group/compare?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
                    break;
                case 'group-merger':
                    url = `/api/order/dubbo/version-group/group-merger`;
                    break;
                default:
                    url = `/api/order/dubbo/version-group/v1-default?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
            }
            await this.callWithResultDisplay(url, `dubbo-version-group-${this.dubboVersionGroupType}`, 'dubbo-version-group')
        },
        async testLoadBalanceStrategy() {
            const strategy = this.loadBalanceStrategy;
            const strategyNames = {
                'random': '随机策略',
                'roundrobin': '轮询策略',
                'consistenthash': '一致性哈希策略',
                'leastactive': '最小活跃数策略',
                'shortestresponse': '最短响应时间策略'
            };
            
            let url;
            if (strategy === 'consistenthash') {
                const param = this.loadBalanceParam || 'test-001';
                url = `/api/order/dubbo/loadbalance/${strategy}?requestCount=${this.loadBalanceRequestCount || 20}&param=${param}`;
            } else {
                url = `/api/order/dubbo/loadbalance/${strategy}?requestCount=${this.loadBalanceRequestCount || 20}`;
            }
            
            const startTime = Date.now();
            try {
                const response = await fetch(url);
                const data = await response.json();
                const endTime = Date.now();
                
                this.loadBalanceResult = {
                    status: data.code === 200 ? 'success' : 'error',
                    title: `${strategyNames[strategy]}测试结果`,
                    message: data.message || (data.data ? JSON.stringify(data.data).substring(0, 200) + '...' : '无返回信息'),
                    timestamp: new Date().toLocaleTimeString(),
                    responseTime: endTime - startTime,
                    code: response.status,
                    data: data,
                    endpoint: url
                };
            } catch (error) {
                const endTime = Date.now();
                this.loadBalanceResult = {
                    status: 'error',
                    title: `${strategyNames[strategy]}测试结果`,
                    message: `请求失败: ${error.message}`,
                    timestamp: new Date().toLocaleTimeString(),
                    responseTime: endTime - startTime,
                    code: null,
                    endpoint: url
                };
            }
        },
        async testFeign() {
            await this.callWithResultDisplay(this.endpoint('feign'), 'feign', 'sca-feign')
        },
        async testLoadBalance() { await this.callWithResultDisplay(this.endpoint('load-balance'), 'load-balance', 'load-balance') },
        async testAsync() { await this.callWithResultDisplay(this.endpoint('async'), 'async', 'async') },
        async testTracing() { await this.callWithResultDisplay(this.endpoint('tracing'), 'tracing', 'tracing') },
        async testRocketMqPublishBasic() { await this.callWithResultDisplay(this.endpoint('rocketmq/publish-basic'), 'rocketmq-publish-basic', 'rocketmq-business-chain') },
        async testRocketMqRetry() { await this.callWithResultDisplay(this.endpoint('rocketmq/retry'), 'rocketmq-retry', 'rocketmq-business-chain') },
        async testRocketMqDlq() { await this.callWithResultDisplay(this.endpoint('rocketmq/dlq'), 'rocketmq-dlq', 'rocketmq-business-chain') },
        async testRocketMqIdempotent() { await this.callWithResultDisplay(this.endpoint('rocketmq/idempotent'), 'rocketmq-idempotent', 'rocketmq-business-chain') },
        async testRocketMqOrderly() { await this.callWithResultDisplay(this.endpoint('rocketmq/orderly'), 'rocketmq-orderly', 'rocketmq-business-chain') },
        async testRocketMqDelayClose() { await this.callWithResultDisplay(this.endpoint('rocketmq/delay-close'), 'rocketmq-delay-close', 'rocketmq-business-chain') },
        async testRocketMqTxSend() { await this.callWithResultDisplay(this.endpoint('rocketmq/tx/send'), 'rocketmq-tx-send', 'rocketmq-business-chain') },
        async testRocketMqTxCheck() { await this.callWithResultDisplay(this.endpoint('rocketmq/tx/check'), 'rocketmq-tx-check', 'rocketmq-business-chain') },
        async testRocketMqTagFilter() { await this.callWithResultDisplay(this.endpoint('rocketmq/tag-filter'), 'rocketmq-tag-filter', 'rocketmq-business-chain') },
        async testRocketMqReplayDlq() { await this.callWithResultDisplay(this.endpoint('rocketmq/replay-dlq'), 'rocketmq-replay-dlq', 'rocketmq-business-chain') },
        
        async testRedisString() {
            await this.callWithResultDisplay(this.endpoint('redis/string'), 'Redis String操作', 'redis-data-structures')
        },
        async testRedisHash() {
            await this.callWithResultDisplay(this.endpoint('redis/hash'), 'Redis Hash操作', 'redis-data-structures')
        },
        async testRedisList() {
            await this.callWithResultDisplay(this.endpoint('redis/list'), 'Redis List操作', 'redis-data-structures')
        },
        async testRedisSet() {
            await this.callWithResultDisplay(this.endpoint('redis/set'), 'Redis Set操作', 'redis-data-structures')
        },
        async testRedisZSet() {
            await this.callWithResultDisplay(this.endpoint('redis/zset'), 'Redis ZSet操作', 'redis-data-structures')
        },
        async testRedisBasicLock() {
            await this.callWithResultDisplay(this.endpoint('redis/lock/basic'), 'Redis基础分布式锁', 'redis-distributed-lock')
        },
        async testRedisReentrantLock() {
            await this.callWithResultDisplay(this.endpoint('redis/lock/reentrant'), 'Redis可重入锁', 'redis-distributed-lock')
        },
        async testRedisLockRenewal() {
            await this.callWithResultDisplay(this.endpoint('redis/lock/renewal'), 'Redis锁续期', 'redis-distributed-lock')
        },
        async testRedisRedLock() {
            await this.callWithResultDisplay(this.endpoint('redis/lock/redlock'), 'Redis RedLock算法', 'redis-distributed-lock')
        },
        async testRedisCacheWarmup() {
            await this.callWithResultDisplay(this.endpoint('redis/cache/warmup'), 'Redis缓存预热', 'redis-cache-issues')
        },
        async testRedisCachePenetration() {
            await this.callWithResultDisplay(this.endpoint('redis/cache/penetration'), 'Redis缓存穿透', 'redis-cache-issues')
        },
        async testRedisCacheBreakdown() {
            await this.callWithResultDisplay(this.endpoint('redis/cache/breakdown'), 'Redis缓存击穿', 'redis-cache-issues')
        },
        async testRedisCacheAvalanche() {
            await this.callWithResultDisplay(this.endpoint('redis/cache/avalanche'), 'Redis缓存雪崩', 'redis-cache-issues')
        },
        async testRedisFixedWindow() {
            await this.callWithResultDisplay(this.endpoint('redis/rate-limit/fixed'), 'Redis固定窗口限流', 'redis-rate-limit')
        },
        async testRedisSlidingWindow() {
            await this.callWithResultDisplay(this.endpoint('redis/rate-limit/sliding'), 'Redis滑动窗口限流', 'redis-rate-limit')
        },
        async testRedisTokenBucket() {
            await this.callWithResultDisplay(this.endpoint('redis/rate-limit/token'), 'Redis令牌桶限流', 'redis-rate-limit')
        },
        async testRedisLeakyBucket() {
            await this.callWithResultDisplay(this.endpoint('redis/rate-limit/leaky'), 'Redis漏桶限流', 'redis-rate-limit')
        },
        async testRedisPipeline() {
            await this.callWithResultDisplay(this.endpoint('redis/pipeline'), 'Redis Pipeline性能测试', 'redis-pipeline')
        },
        async testRedisTransaction() {
            await this.callWithResultDisplay(this.endpoint('redis/transaction'), 'Redis事务测试', 'redis-transaction')
        },
        async testRedisWatch() {
            await this.callWithResultDisplay(this.endpoint('redis/watch'), 'Redis乐观锁', 'redis-transaction')
        },
        async testRedisLua() {
            await this.callWithResultDisplay(this.endpoint('redis/lua'), 'Redis Lua脚本', 'redis-transaction')
        },
        async testRedisPubSub() {
            await this.callWithResultDisplay(this.endpoint('redis/pubsub'), 'Redis发布订阅', 'redis-pubsub')
        },
        async testRedisPatternPubSub() {
            await this.callWithResultDisplay(this.endpoint('redis/pubsub/pattern'), 'Redis模式订阅', 'redis-pubsub')
        },
        async testRedisRDB() {
            await this.callWithResultDisplay(this.endpoint('redis/persistence/rdb'), 'Redis RDB持久化', 'redis-persistence')
        },
        async testRedisAOF() {
            await this.callWithResultDisplay(this.endpoint('redis/persistence/aof'), 'Redis AOF持久化', 'redis-persistence')
        },
        async testRedisMonitor() {
            await this.callWithResultDisplay(this.endpoint('redis/monitor'), 'Redis性能监控', 'redis-persistence')
        },
        async testGatewayRouting() {
            await this.callWithResultDisplay(this.endpoint('gateway-routing'), 'gateway-routing', 'higress-routing')
        },
        async testCircuitBreaker(simulateError) {
            const url = this.endpoint('circuit-breaker') + `?simulateError=${simulateError}`
            await this.callWithResultDisplay(url, 'circuit-breaker', 'circuit-breaker')
        },
        async testTimeoutRetry() {
            await this.callWithResultDisplay(this.endpoint('timeout-retry') + '?productId=88888', 'timeout-retry', 'timeout-retry')
        },
        async testFeignInterceptor() {
            await this.callWithResultDisplay(this.endpoint('feign-interceptor'), 'feign-interceptor', 'feign-interceptor')
        },
        async testConfigRefresh() {
            await this.callWithResultDisplay(this.endpoint('config-refresh'), 'config-refresh', 'config-refresh')
        },
        async testHealthCheck() {
            await this.callWithResultDisplay(this.endpoint('health-check'), 'health-check', 'health-check')
        },
        async testFeignEnhanced(mode) {
            let productId = this.feignProductId;
            if (mode === 'error') {
                productId = 99999;
            } else if (mode === 'degrade') {
                productId = 88888;
            }
            // 临时修改feignProductId的值，然后调用endpoint函数
            const originalProductId = this.feignProductId;
            this.feignProductId = productId;
            const url = this.endpoint('feign-enhanced');
            // 恢复原始值
            this.feignProductId = originalProductId;
            await this.callWithResultDisplay(url, 'feign-enhanced', 'feign-enhanced')
        },
        async testFeignVsDubbo() {
            const urlF = this.endpoint('compare-feign')
            const urlD = this.endpoint('compare-dubbo')
            for (let i = 0; i < this.compareTimes; i++) {
                await Promise.all([
                    this.callWithResultDisplay(urlF, 'compare-feign', 'compare-feign'),
                    this.callWithResultDisplay(urlD, 'compare-dubbo', 'compare-dubbo')
                ])
            }
        },
        async testProtocolCompare() {
            const url = this.endpoint('protocol-compare') + `?productId=${this.dubboProductId}&requestCount=${this.compareTimes}`;
            await this.callWithResultDisplay(url, 'protocol-compare', 'protocol-compare')
        },
        async testProtocolDubbo() {
            if (this.loadingStates['protocol-dubbo']) return
            this.loadingStates['protocol-dubbo'] = true
            const url = this.endpoint('protocol-dubbo') + `?productId=${this.dubboProductId}`;
            await this.callWithResultDisplay(url, 'protocol-dubbo', 'protocol-dubbo')
            this.loadingStates['protocol-dubbo'] = false
        },
        async testProtocolTriple() {
            if (this.loadingStates['protocol-triple']) return
            this.loadingStates['protocol-triple'] = true
            const url = this.endpoint('protocol-triple') + `?productId=${this.dubboProductId}`;
            await this.callWithResultDisplay(url, 'protocol-triple', 'protocol-triple')
            this.loadingStates['protocol-triple'] = false
        },
        async testProtocolRest() {
            if (this.loadingStates['protocol-rest']) return
            this.loadingStates['protocol-rest'] = true
            const url = this.endpoint('protocol-rest') + `?productId=${this.dubboProductId}`;
            await this.callWithResultDisplay(url, 'protocol-rest', 'protocol-rest')
            this.loadingStates['protocol-rest'] = false
        },
        async call(url, type) {
            try {
                const s = performance.now()
                const res = await axios.get(url)
                const payload = res.data
                const ok = payload && typeof payload === 'object' && 'code' in payload ? (payload.code === 200) : (res.status === 200)
                const t = this.formatTime(new Date())
                const rt = Math.round(performance.now() - s)

                const item = {
                    id: Date.now() + Math.random(),
                    time: t,
                    type: type,
                    status: ok ? '成功' : '失败',
                    code: ok ? 200 : (payload.code || res.status),
                    msg: ok ? (payload.msg || 'OK') : (payload.msg || res.statusText),
                    data: payload.data || payload,
                    endpoint: url,
                    rt
                }

                this.history.unshift(item)
                if (this.history.length > 100) this.history.pop()
            } catch (e) {
                const t = this.formatTime(new Date())
                this.history.unshift({
                    id: Date.now() + Math.random(),
                    time: t,
                    type,
                    status: '失败',
                    code: e.response ? e.response.status : 0,
                    msg: e.message,
                    endpoint: url,
                    rt: 0
                })
            }
        },
        viewDetail(item) {
            this.detailView = JSON.stringify(item, null, 2)
            this.showDetailModal = true
            this.$nextTick(() => {
                this.highlightCode()
            })
        },
        showResultDetail(testId) {
            if (testId === 'loadbalance') {
                const result = this.loadBalanceResult
                if (result && result.data) {
                    this.detailView = JSON.stringify(result.data, null, 2)
                    this.showDetailModal = true
                    this.$nextTick(() => {
                        this.highlightCode()
                    })
                } else if (result) {
                    const detailInfo = {
                        testId: testId,
                        status: result.status,
                        title: result.title,
                        message: result.message,
                        timestamp: result.timestamp,
                        responseTime: result.responseTime,
                        code: result.code,
                        endpoint: result.endpoint
                    }
                    this.detailView = JSON.stringify(detailInfo, null, 2)
                    this.showDetailModal = true
                    this.$nextTick(() => {
                        this.highlightCode()
                    })
                }
                return
            }
            
            const result = this.resultDisplays[testId]
            if (result && result.data) {
                this.detailView = JSON.stringify(result.data, null, 2)
                this.showDetailModal = true
                this.$nextTick(() => {
                    this.highlightCode()
                })
            } else if (result) {
                const detailInfo = {
                    testId: testId,
                    status: result.status,
                    title: result.title,
                    message: result.message,
                    timestamp: result.timestamp,
                    responseTime: result.responseTime,
                    code: result.code,
                    endpoint: result.endpoint
                }
                this.detailView = JSON.stringify(detailInfo, null, 2)
                this.showDetailModal = true
                this.$nextTick(() => {
                    this.highlightCode()
                })
            }
        },
        showStrategyDetails(strategy) {
            const strategyDetails = {
                random: {
                    title: 'Random（随机策略）—— Dubbo 的默认策略',
                    sections: [
                        {
                            label: '工作原理',
                            content: '随机策略根据后端服务器设定的<strong>权重（Weight）</strong>来随机选择一台进行调用。假设有三台服务器 A, B, C，权重分别为 1, 2, 3。Dubbo 会计算一个总权重（1+2+3=6），然后在 0 到 6 之间生成一个随机数。根据随机数落在哪个区间来决定调用哪台机器。'
                        },
                        {
                            label: '优点',
                            content: '• <strong>性能极高：</strong>计算量小，不需要维护额外的状态（如计数器）。<br>• <strong>自动均衡：</strong>在调用量足够大的情况下，请求分布会自动趋近于权重的比例。<br>• <strong>避免拥堵：</strong>如果某台机器瞬间变慢，随机性可以避免后续所有请求像"排队"一样死磕在某台机器上。'
                        },
                        {
                            label: '缺点',
                            content: '<strong>瞬时分布不均：</strong>在请求量较少时，可能会出现连续多次抽中同一台机器的情况。'
                        }
                    ]
                },
                roundrobin: {
                    title: 'RoundRobin（轮询策略）',
                    sections: [
                        {
                            label: '工作原理',
                            content: '轮询策略按顺序循环调用后端服务器，并同样支持权重。Dubbo 采用的是<strong>加权轮询（Weighted Round Robin）</strong>。它会维护一个调用计数。如果三台机器 A, B, C 权重一致，请求顺序就是 A -> B -> C -> A...。如果权重不同，它会确保在一个循环周期内，高权重的机器分配到更多的请求，且分发尽量平滑。'
                        },
                        {
                            label: '优点',
                            content: '<strong>绝对均匀：</strong>请求分发非常死板且精准，能够严格保证每台机器接收到的请求数符合比例。'
                        },
                        {
                            label: '缺点',
                            content: '• <strong>维护成本：</strong>需要维护请求计数状态。<br>• <strong>慢连接堆积：</strong>这是轮询最大的弊端。如果服务器 A 反应极慢，由于轮询是强迫性的，请求依然会按计划发给 A，导致 A 上的请求越积越多，最终可能拖垮整个链路（随机策略则可以通过概率规避部分影响）。'
                        }
                    ]
                },
                consistenthash: {
                    title: 'ConsistentHash（一致性哈希策略）',
                    sections: [
                        {
                            label: '核心特性',
                            content: '在 Dubbo 的负载均衡体系中，ConsistentHash（一致性哈希）是一种非常特殊且强大的策略。它与 Random 或 RoundRobin 不同，它不追求"绝对的平均"，而是追求<strong>"请求的确定性"</strong>。简单来说，一致性哈希能保证：相同的请求参数，总是发往同一台提供者。'
                        },
                        {
                            label: '核心原理：哈希环',
                            content: '一致性哈希将整个哈希值空间组织成一个虚拟的圆环（范围是 0 到 2<sup>32</sup>-1）：<br>• <strong>节点映射：</strong>将每台服务器（Provider）的 IP 或 ID 进行哈希计算，映射到环上的某个位置。<br>• <strong>请求映射：</strong>当请求进来时，根据指定的请求参数进行哈希计算，映射到环上的一个点。<br>• <strong>顺时针寻址：</strong>从请求映射的点开始，在环上顺时针行走，遇到的第一台服务器就是该请求的处理者。'
                        },
                        {
                            label: 'Dubbo 引入的关键特性：虚拟节点（Virtual Nodes）',
                            content: '为了解决"哈希倾斜"（即服务器在环上分布不均，导致某台机器压力过大）的问题，Dubbo 默认引入了虚拟节点：它会对每台物理服务器模拟出多个"影子"节点（默认 160 份）。这样服务器在环上的分布就会非常均匀，即便某一台机器下线，请求也会平摊给剩余的所有机器，而不是全部挤向下一台。'
                        },
                        {
                            label: '使用场景：为什么需要它？',
                            content: '• <strong>服务端本地缓存：</strong>如果 Provider 会在本地缓存某些昂贵的数据（如复杂的视频版权规则、用户信息），一致性哈希能确保相同用户的请求一直打到同一台机器，从而极大地提高缓存命中率。<br>• <strong>有状态服务：</strong>某些操作需要连续性（比如分片上传视频），确保同一个任务的后续请求都能找到之前的处理者。<br>• <strong>降低抖动风险：</strong>当集群扩容或缩容（增加/减少机器）时，一致性哈希只会导致局部请求失效（重新路由），而不会像普通的 hash(key) % N 策略那样导致全网缓存失效。'
                        },
                        {
                            label: '关键配置参数',
                            content: '在 Dubbo 中使用一致性哈希，需要关注几个关键配置：<br>• <strong>loadbalance="consistenthash"</strong>：开启一致性哈希负载均衡。<br>• <strong>hash.arguments</strong>：最重要配置。指定用第几个参数做哈希（如 0 表示第一个参数）。相同的参数值会被路由到同一台机器。<br>• <strong>hash.nodes</strong>：虚拟节点数，默认 160，通常不需要改动。'
                        }
                    ]
                },
                leastactive: {
                    title: 'LeastActive（最小活跃数策略）',
                    sections: [
                        {
                            label: '工作原理',
                            content: '最小活跃数策略的核心思想是"能者多劳"。它优先选择当前活跃请求数最少的服务器。活跃请求数是指服务器当前正在处理的请求数量。如果活跃数相同，则采用加权随机策略选择。'
                        },
                        {
                            label: '优点',
                            content: '• <strong>智能调度：</strong>自动将请求分配给负载较轻的服务器，避免某些服务器过载。<br>• <strong>自适应能力：</strong>能够根据服务器的实际处理能力动态调整请求分配。<br>• <strong>提高整体性能：</strong>充分利用高性能服务器的处理能力。'
                        },
                        {
                            label: '缺点',
                            content: '<strong>需要维护状态：</strong>需要实时跟踪每台服务器的活跃请求数，增加了复杂度。'
                        }
                    ]
                },
                shortestresponse: {
                    title: 'ShortestResponse（最短响应时间策略）',
                    sections: [
                        {
                            label: '工作原理',
                            content: '最短响应时间策略优先选择响应时间最短的服务器。它会记录每台服务器的平均响应时间，并选择响应时间最短的服务器。如果响应时间相同，则采用加权随机策略选择。'
                        },
                        {
                            label: '优点',
                            content: '• <strong>关注用户体验：</strong>优先选择响应快的服务器，提高用户体验。<br>• <strong>动态调整：</strong>根据实际响应时间动态调整请求分配。<br>• <strong>避免慢节点：</strong>自动避开响应慢的服务器，提高整体性能。'
                        },
                        {
                            label: '缺点',
                            content: '• <strong>需要历史数据：</strong>需要收集和维护每台服务器的响应时间历史。<br>• <strong>冷启动问题：</strong>新服务器可能因为没有历史数据而被冷落。'
                        }
                    ]
                },
                p2c: {
                    title: 'P2C（Power of Two Choice）',
                    sections: [
                        {
                            label: '工作原理',
                            content: 'P2C（Power of Two Choice）算法是一种简单而有效的负载均衡策略。它随机选择两台服务器，然后比较这两台服务器的连接数，选择连接数较少的那一台。这种策略在理论上可以显著降低最大负载。'
                        },
                        {
                            label: '优点',
                            content: '• <strong>简单高效：</strong>算法简单，计算量小，性能高。<br>• <strong>负载均衡效果好：</strong>理论证明可以显著降低最大负载，比纯随机策略更均衡。<br>• <strong>无需复杂状态：</strong>只需要知道每台服务器的连接数即可。'
                        },
                        {
                            label: '缺点',
                            content: '<strong>随机性：</strong>虽然比纯随机好，但仍然有一定的随机性，无法做到绝对的均衡。'
                        }
                    ]
                },
                adaptive: {
                    title: 'Adaptive（自适应负载均衡）',
                    sections: [
                        {
                            label: '工作原理',
                            content: '自适应负载均衡策略在 P2C 算法的基础上，进一步考虑了服务器的负载情况。它随机选择两台服务器，然后比较这两台服务器的综合负载指标（如连接数、CPU 使用率、内存使用率等），选择负载较低的那一台。这种策略能够更智能地分配请求，充分利用服务器资源。'
                        },
                        {
                            label: '优点',
                            content: '• <strong>智能调度：</strong>综合考虑多种负载指标，做出更智能的决策。<br>• <strong>动态适应：</strong>能够根据服务器的实时负载情况动态调整请求分配。<br>• <strong>资源利用率高：</strong>充分利用服务器资源，提高整体性能。'
                        },
                        {
                            label: '缺点',
                            content: '• <strong>复杂度高：</strong>需要收集和维护多种负载指标，实现复杂度较高。<br>• <strong>性能开销：</strong>需要额外的计算和存储开销来维护负载指标。'
                        }
                    ]
                }
            }
            
            this.currentStrategyDetails = strategyDetails[strategy]
            this.showStrategyModal = true
        },
        closeStrategyModal() {
            this.showStrategyModal = false
            this.currentStrategyDetails = null
        },
        highlightCode() {
            const display = document.getElementById('json-display')
            if (display && this.detailView) {
                display.innerHTML = this.syntaxHighlightJSON(this.detailView)
            }
        },
        syntaxHighlightJSON(json) {
            if (typeof json !== 'string') {
                json = JSON.stringify(json, undefined, 2)
            }
            json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            return json.replace(/(["'])(?:(?=(\\?)\2).)*?\1|\b(true|false|null)\b|\b-?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?\b/g, function (match) {
                var cls = 'number'
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key'
                    } else {
                        cls = 'string'
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean'
                } else if (/null/.test(match)) {
                    cls = 'null'
                }
                return '<span class="json-' + cls + '">' + match + '</span>'
            })
        },
        reset() {
            this.history = []
            this.resultDisplays = {}
        },

        // Individual result display methods
        setResultDisplay(testId, result) {
            this.resultDisplays = {
                ...this.resultDisplays,
                [testId]: result
            }
        },

        clearResultDisplay(testId) {
            const newDisplays = { ...this.resultDisplays }
            delete newDisplays[testId]
            this.resultDisplays = newDisplays
        },

        getResultDisplay(testId) {
            return this.resultDisplays[testId] || null
        },

        formatResultForDisplay(item) {
            if (!item) return null

            return {
                status: item.status,
                code: item.code,
                message: item.msg,
                data: item.data,
                responseTime: item.rt,
                timestamp: item.time,
                type: item.type
            }
        },

        async callWithResultDisplay(url, type, testId) {
            // Clear previous result and set loading state
            this.setResultDisplay(testId, {
                status: 'loading',
                message: '正在执行测试...',
                timestamp: this.formatTime(new Date()),
                endpoint: url
            })

            try {
                const s = performance.now()
                const res = await axios.get(url)
                const payload = res.data
                const ok = payload && typeof payload === 'object' && 'code' in payload ? (payload.code === 200) : (res.status === 200)
                const t = this.formatTime(new Date())
                const rt = Math.round(performance.now() - s)

                const item = {
                    id: Date.now() + Math.random(),
                    time: t,
                    type: type,
                    status: ok ? '成功' : '失败',
                    code: ok ? 200 : (payload.code || res.status),
                    msg: ok ? (payload.msg || 'OK') : (payload.msg || res.statusText),
                    data: payload.data || payload,
                    endpoint: url,
                    rt
                }

                // Remove old results of the same type to avoid duplicates
                for (let i = this.history.length - 1; i >= 0; i--) {
                    if (this.history[i].type === type) {
                        this.history.splice(i, 1)
                    }
                }

                // Add to main history
                this.history.unshift(item)
                if (this.history.length > 100) this.history.pop()

                // Update individual result display
                this.setResultDisplay(testId, {
                    status: ok ? 'success' : 'error',
                    code: item.code,
                    message: item.msg,
                    data: item.data,
                    responseTime: rt,
                    timestamp: t,
                    type: type,
                    endpoint: url,
                    rawData: item  // 存储完整的数据对象，用于详情显示
                })

            } catch (e) {
                const t = this.formatTime(new Date())
                const errorItem = {
                    id: Date.now() + Math.random(),
                    time: t,
                    type,
                    status: '失败',
                    code: e.response ? e.response.status : 0,
                    msg: e.message,
                    endpoint: url,
                    rt: 0
                }

                // Remove old results of the same type to avoid duplicates
                for (let i = this.history.length - 1; i >= 0; i--) {
                    if (this.history[i].type === type) {
                        this.history.splice(i, 1)
                    }
                }

                // Add to main history
                this.history.unshift(errorItem)
                if (this.history.length > 100) this.history.pop()

                // Update individual result display
                this.setResultDisplay(testId, {
                    status: 'error',
                    code: errorItem.code,
                    message: errorItem.msg,
                    responseTime: 0,
                    timestamp: t,
                    type: type,
                    endpoint: url,
                    rawData: errorItem  // 存储完整的错误数据对象，用于详情显示
                })
            }

        },
        getApiInfo() {
            const apis = {
                sentinel: { method: 'GET', path: '/api/order/rateLimit/qps', params: '', description: '测试 Sentinel 的 QPS 限流规则，限制每秒请求数量' },
                nacos: { method: 'GET', path: '/api/order/demo/nacos/services', params: '', description: '查询 Nacos 中注册的所有服务实例' },
                dubbo: { method: 'GET', path: '/api/order/dubbo/call-sync?productId=1', params: 'productId', description: '使用 Dubbo 协议远程调用 Product 服务' },
                rocketmq: { method: 'GET', path: '/api/order/demo/rocketmq/publish-basic', params: '', description: '按订单链路演示 RocketMQ 普通消息、重试、DLQ 与事务消息' },
                seata: { method: 'GET', path: '/api/business/purchase/tcc/verify?userId=U1001&commodityCode=P0001&count=1&fail=false', params: 'userId, commodityCode, count, fail', description: '执行 Seata TCC 分布式事务并返回前后状态证据' },
                gateway: { method: 'GET', path: '/api/order/demo/gateway-routing', params: '', description: '验证 Spring Cloud Gateway 路由规则配置是否生效' },
                higress: { method: 'GET', path: '/api/order/demo/gateway-routing', params: '', description: '验证网关路由规则配置是否生效' },
                sca: { method: 'GET', path: '/api/order/demo/feign/call-enhanced?productId=1', params: 'productId', description: '使用 OpenFeign + LoadBalancer 调用 Product 服务' },
                opentelemetry: { method: 'GET', path: '/api/order/demo/tracing', params: '', description: '生成模拟的应用链路数据用于可观测性演示' }
            }
            return apis[this.activeComponent] || { method: 'GET', path: '/api/order/demo', params: '', description: '微服务 API 演示' }
        },
        getConcept() {
            const concepts = {
                sentinel: {
                    title: '💡 Sentinel 限流原理',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li>基于滑动时间窗口算法，精确计算流量</li>
                            <li><strong>QPS 限流</strong>：限制单位时间内的请求数，超出直接拒绝</li>
                            <li><strong>线程隔离</strong>：限制并发执行线程数，等待队列缓冲请求</li>
                            <li><strong>熔断降级</strong>：异常率过高时自动断路，避免级联故障</li>
                            <li><strong>热点参数限流</strong>：对特定参数值单独限流，防护热点数据</li>
                        </ul>
                    `
                },
                nacos: {
                    title: '💡 Nacos 服务治理',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li>基于 Raft 算法保证数据一致性</li>
                            <li><strong>服务注册</strong>：应用启动自动注册到 Nacos，上报实例信息</li>
                            <li><strong>服务发现</strong>：消费者从 Nacos 拉取可用实例并订阅变化</li>
                            <li><strong>配置管理</strong>：支持热更新，无需重启应用即可生效</li>
                            <li><strong>健康检查</strong>：定期检测实例健康状态，故障时自动下线</li>
                        </ul>
                    `
                },
                dubbo: {
                    title: '💡 Dubbo RPC 调用',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li>基于 Hessian2 序列化，性能比 JSON 高 20+ 倍</li>
                            <li><strong>协议</strong>：使用 TCP 长连接，支持 Netty 传输</li>
                            <li><strong>负载均衡</strong>：支持轮询、加权轮询、随机等策略</li>
                            <li><strong>容错机制</strong>：Failover、Failfast、Failsafe 等自动降级</li>
                            <li><strong>适用场景</strong>：内部高性能、低延迟的服务调用</li>
                        </ul>
                    `
                },
                rocketmq: {
                    title: '💡 RocketMQ 业务链路',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li><strong>核心价值</strong>：异步解耦、削峰填谷、失败隔离、最终一致</li>
                            <li><strong>普通消息</strong>：下单事件异步通知库存、营销等下游系统</li>
                            <li><strong>重试 + DLQ</strong>：消费失败自动重试，重试耗尽进入死信队列</li>
                            <li><strong>幂等与顺序</strong>：消费端去重防止重复执行，同订单按队列保证有序</li>
                            <li><strong>事务与回查</strong>：半消息 + 本地事务 + 回查确保最终一致性</li>
                        </ul>
                    `
                },
                seata: {
                    title: '💡 Seata 分布式事务',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li><strong>TCC 模式</strong>：强一致性，适合支付等核心业务</li>
                            <li>Try：各参与方预留资源（锁库存、冻结账户）</li>
                            <li>Confirm：全部预留成功后，执行真正业务逻辑</li>
                            <li>Cancel：任何环节失败时回滚，恢复资源</li>
                            <li><strong>其他模式</strong>：AT（自动解析 SQL）、SAGA（事件驱动）</li>
                        </ul>
                    `
                },
                higress: {
                    title: '💡 Higress 网关',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li>基于 Envoy 代理的云原生网关</li>
                            <li><strong>流量入口</strong>：所有外部请求必须通过网关</li>
                            <li><strong>统一鉴权</strong>：在网关层验证 JWT Token 或其他凭证</li>
                            <li><strong>限流保护</strong>：网关层限流，保护后端服务</li>
                            <li><strong>黑白名单</strong>：支持 IP、User-Agent 等维度的访问控制</li>
                        </ul>
                    `
                },
                gateway: {
                    title: '💡 Spring Cloud Gateway',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li>基于 Spring WebFlux 的响应式网关</li>
                            <li><strong>动态路由</strong>：基于 Predicate 匹配的动态路由配置</li>
                            <li><strong>过滤器</strong>：前置和后置过滤器，支持请求修改</li>
                            <li><strong>负载均衡</strong>：集成 Ribbon 实现客户端负载均衡</li>
                            <li><strong>限流熔断</strong>：集成 Sentinel 实现限流和熔断</li>
                        </ul>
                    `
                },
                sca: {
                    title: '💡 Spring Cloud Alibaba',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li><strong>OpenFeign</strong>：声明式 REST 调用，自动负载均衡和容错</li>
                            <li><strong>LoadBalancer</strong>：客户端负载均衡，支持加权轮询</li>
                            <li><strong>Sentinel</strong>：熔断限流，保护调用端</li>
                            <li><strong>超时控制</strong>：支持读超时、连接超时分别配置</li>
                            <li><strong>Fallback 机制</strong>：调用失败时返回备用方案</li>
                        </ul>
                    `
                },
                opentelemetry: {
                    title: '💡 OpenTelemetry 可观测性',
                    content: `
                        <ul style="margin:0; padding-left:20px;">
                            <li>统一采集 Trace、Metric、Log 三大支柱数据</li>
                            <li><strong>Trace（链路）</strong>：追踪请求在分布式系统中的完整路径</li>
                            <li><strong>Metric（指标）</strong>：系统性能数据（CPU、内存、QPS 等）</li>
                            <li><strong>Log（日志）</strong>：应用运行日志，支持结构化存储</li>
                            <li><strong>后端接口</strong>：支持导出到 Jaeger、Skywalking 等平台</li>
                        </ul>
                    `
                }
            }
            return concepts[this.activeComponent] || {}
        },
        async testAll() {
            const comp = this.activeComponent
            const tests = {
                sentinel: ['testQps', 'testThread', 'testHotspot', 'testDegrade'],
                nacos: ['testNacosServices', 'testNacosConfig'],
                dubbo: ['testDubboSync', 'testDubboBatch', 'testDubboListAll', 'testDubboTimeout', 'testDubboException', 'testDubboAsync', 'testDubboRegion', 'testDubboConcurrency', 'testDubboActives', 'testDubboLeastActive'],
                seata: ['testTccOk', 'testTccFail'],
                sca: ['testFeignVsDubbo', 'testLoadBalance', 'testAsync'],
                rocketmq: ['testRocketMqPublishBasic', 'testRocketMqRetry', 'testRocketMqDlq', 'testRocketMqIdempotent', 'testRocketMqOrderly', 'testRocketMqDelayClose', 'testRocketMqTxSend', 'testRocketMqTxCheck', 'testRocketMqTagFilter', 'testRocketMqReplayDlq'],
                higress: ['testGatewayRouting'],
                opentelemetry: ['testTracing']
            }
            const compTests = tests[comp] || []

            for (const testName of compTests) {
                if (typeof this[testName] === 'function') {
                    try { await this[testName]() } catch (e) { console.error(testName, e) }
                } else if (testName === 'testQps') {
                    await this.trigger('qps', this.qpsTimes)
                } else if (testName === 'testHotspot') {
                    await this.trigger('hot', this.hotTimes)
                } else if (testName === 'testTccOk') {
                    await this.call(this.endpoint('tcc-ok'), 'tcc-ok')
                } else if (testName === 'testTccFail') {
                    await this.call(this.endpoint('tcc-fail'), 'tcc-fail')
                }
            }
        },

        // 初始化代码高亮
        initCodeHighlighting() {
            this.$nextTick(() => {
                // 使用本地轻量级高亮库
                if (typeof window.simpleHighlight !== 'undefined') {
                    window.simpleHighlight.highlightAll();
                    console.log('代码高亮已初始化');
                } else {
                    console.warn('simpleHighlight 未加载');
                }
            });
        },

        // 复制代码功能
        copyCode(button) {
            const codeBlock = button.closest('.code-block')
            const code = codeBlock.querySelector('code').textContent

            navigator.clipboard.writeText(code).then(() => {
                button.textContent = '已复制'
                button.classList.add('copied')

                setTimeout(() => {
                    button.textContent = '复制'
                    button.classList.remove('copied')
                }, 2000)
            }).catch(err => {
                console.error('复制失败:', err)
            })
        },
        
        // 主题切换功能
        toggleTheme() {
            this.isDarkMode = !this.isDarkMode;
            this.applyTheme();
        },
        
        // 应用主题
        applyTheme() {
            const htmlElement = document.documentElement;
            
            if (this.isDarkMode) {
                htmlElement.classList.add('dark');
                localStorage.setItem('theme-mode', 'dark');
            } else {
                htmlElement.classList.remove('dark');
                localStorage.setItem('theme-mode', 'light');
            }
        },
    },
    mounted() {
        if (!this.isCoreComponent(this.activeComponent) && !this.showRoadmapSection) {
            this.activeComponent = 'dubbo'
            this.activeSection = null
            localStorage.setItem('service-demo-component', this.activeComponent)
            localStorage.removeItem('service-demo-section')
        }

        // 初始化主题
        this.applyTheme();
        
        // 点击外部关闭下拉菜单
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.menu-dropdown')) {
                this.closeDropdown();
            }
        });
        
        // 根据当前选中的组件展开对应的菜单组
        this.updateActivePanoramaTab(this.activeComponent);
        
        // 根据当前选中的组件展开对应的二级菜单
        if (this.expandedMenus.hasOwnProperty(this.activeComponent)) {
            this.expandedMenus[this.activeComponent] = true
        }
        
        // 如果activeSection为null，设置为该组件的默认测试场景
        if (!this.activeSection && this.defaultSections[this.activeComponent]) {
            this.activeSection = this.defaultSections[this.activeComponent]
            localStorage.setItem('service-demo-section', this.activeSection)
        }
        
        // 初始化代码高亮 - 延迟执行确保 DOM 完全渲染
        setTimeout(() => {
            this.initCodeHighlighting();
        }, 100);

        // 监听组件切换,重新高亮代码
        this.$watch('activeComponent', () => {
            this.$nextTick(() => {
                setTimeout(() => {
                    this.initCodeHighlighting();
                    // 重新初始化折叠代码块
                    initCollapsibleCode();
                    // 重新添加复制按钮 (如果需要)
                    addCopyButtons();
                }, 50);
            });
        });

        // 监听滚动事件，高亮当前可见的section
        window.addEventListener('scroll', this.handleScroll);
    },
    beforeUnmount() {
        document.removeEventListener('click', this.closeDropdown);
        window.removeEventListener('scroll', this.handleScroll);
    }
})

// 添加复制按钮到代码块
function addCopyButtons() {
    document.querySelectorAll('.code-block:not(.no-copy)').forEach(block => {
        if (!block.querySelector('.copy-btn')) {
            const copyBtn = document.createElement('button')
            copyBtn.className = 'copy-btn'
            copyBtn.textContent = '复制'
            copyBtn.onclick = function () {
                const appInstance = app.config.globalProperties
                if (appInstance.copyCode) {
                    appInstance.copyCode(this)
                }
            }
            block.appendChild(copyBtn)
        }
    })
}

// 初始化折叠代码块
function initCollapsibleCode() {
    document.querySelectorAll('.code-block.collapsible').forEach(block => {
        // 避免重复初始化
        if (block.dataset.collapsibleInit) return;
        block.dataset.collapsibleInit = 'true';

        // 检查高度是否需要折叠 (例如超过 150px)
        if (block.scrollHeight > 150) {
            block.classList.add('collapsed');

            // 创建展开/收起按钮
            const toggleBtn = document.createElement('button');
            toggleBtn.className = 'collapse-btn';
            toggleBtn.innerHTML = '<span>▼</span> 展开代码';

            toggleBtn.onclick = function (e) {
                e.stopPropagation(); // 防止触发其他点击事件
                const isCollapsed = block.classList.contains('collapsed');
                if (isCollapsed) {
                    block.classList.remove('collapsed');
                    block.classList.add('expanded');
                    this.innerHTML = '<span>▲</span> 收起代码';
                } else {
                    block.classList.remove('expanded');
                    block.classList.add('collapsed');
                    this.innerHTML = '<span>▼</span> 展开代码';
                    // 滚动回顶部，避免收起时页面跳动
                    // block.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                }
            };

            block.appendChild(toggleBtn);
        }
    });
}

// 在DOM加载完成后添加复制按钮和初始化折叠
document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        addCopyButtons();
        initCollapsibleCode();
    }, 100)
})

app.mount('#app')
