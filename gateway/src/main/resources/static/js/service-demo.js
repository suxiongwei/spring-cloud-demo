import AppHeader from './components/AppHeader.js'

const { createApp, ref, reactive } = Vue

const app = createApp({
    components: {
        AppHeader
    },
    data() {
        return {
            activePanoramaTab: localStorage.getItem('service-demo-tab') || 'communication',
            activeComponent: localStorage.getItem('service-demo-component') || 'dubbo',
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

            // System
            history: [],
            showDetailModal: false,
            detailView: '',
            comparisonData: [],
            successRate: 0,
            avgResponseTime: 0,
            testedComponents: 0,
            
            // 主题切换
            isDarkMode: localStorage.getItem('theme-mode') === 'dark' || 
                        (!localStorage.getItem('theme-mode') && window.matchMedia('(prefers-color-scheme: dark)').matches),

            // 侧边栏菜单展开状态
            expandedMenus: {
                control: false,
                governance: false,
                communication: false,
                gateway: false,
                data: false
            },

            // 侧边栏宽度
            sidebarWidth: localStorage.getItem('sidebar-width') || 240,

            // Individual result displays for each test scenario
            resultDisplays: {}
        }
    },
    watch: {
        activePanoramaTab(val) { localStorage.setItem('service-demo-tab', val) },
        activeComponent(val) { localStorage.setItem('service-demo-component', val) },
        showDetailModal(val) {
            if (val) {
                document.body.style.overflow = 'hidden'
            } else {
                document.body.style.overflow = ''
            }
        }
    },
    methods: {
        selectComponent(id) {
            this.activeComponent = id
            this.updateActivePanoramaTab(id)
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
            this.expandedMenus[menuKey] = true
        },
        toggleMenu(menuKey) {
            this.expandedMenus[menuKey] = !this.expandedMenus[menuKey]
        },
        startResize(e) {
            e.preventDefault()
            const startX = e.clientX
            const startWidth = this.sidebarWidth
            const resizer = e.target
            resizer.classList.add('resizing')
            
            const onMouseMove = (moveEvent) => {
                const deltaX = moveEvent.clientX - startX
                const newWidth = Math.max(180, Math.min(400, startWidth + deltaX))
                this.sidebarWidth = newWidth
                this.updateSidebarWidth()
            }
            
            const onMouseUp = () => {
                document.removeEventListener('mousemove', onMouseMove)
                document.removeEventListener('mouseup', onMouseUp)
                resizer.classList.remove('resizing')
                localStorage.setItem('sidebar-width', this.sidebarWidth)
            }
            
            document.addEventListener('mousemove', onMouseMove)
            document.addEventListener('mouseup', onMouseUp)
        },
        updateSidebarWidth() {
            const sidebar = document.querySelector('.sidebar')
            const mainContent = document.querySelector('.main-content')
            if (sidebar && mainContent) {
                sidebar.style.width = this.sidebarWidth + 'px'
                mainContent.style.marginLeft = this.sidebarWidth + 'px'
            }
        },
        formatTime(date) {
            return date.toLocaleTimeString('zh-CN', { hour12: false }) + '.' +
                date.getMilliseconds().toString().padStart(3, '0')
        },
        endpoint(t) {
            const map = {
                'qps': '/api/order/demo/flow-control',
                'thread': '/api/order/demo/flow-control',
                'hot': '/api/order/demo/hot-param',
                'degrade': '/api/order/demo/degrade',
                'tcc-ok': '/api/order/seata/tcc/commit',
                'tcc-fail': '/api/order/seata/tcc/rollback',
                'feign': `/api/order/demo/feign/call?productId=${this.feignProductId}`,
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
                'compare-feign': `/api/order/feign/product/1`,
                'compare-dubbo': `/api/order/dubbo/call-sync?productId=1`,
                'nacos-services': '/api/order/demo/nacos/services',
                'nacos-config': '/api/order/demo/nacos-config',
                'load-balance': '/api/order/demo/load-balance',
                'tracing': '/api/order/demo/tracing',
                'gateway-routing': '/api/order/demo/gateway-routing',
                'gateway-auth-pass': '/api/order/config?auth-test=pass',
                'gateway-auth-fail': '/api/order/config?auth-test=reject',
                'async': '/api/order/demo/async-parallel',
                'circuit-breaker': '/api/order/demo/circuit-breaker',
                'timeout-retry': '/api/order/demo/timeout-retry',
                'feign-interceptor': '/api/order/demo/feign-interceptor',
                'config-refresh': '/api/order/demo/config-refresh',
                'health-check': '/api/order/demo/health-check',
                'feign-enhanced': `/api/order/demo/feign/call-enhanced?productId=${this.feignProductId}`
            }
            return map[t] || '/'
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
                    timestamp: this.formatTime(new Date())
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
                        rt: '批量'
                    }

                    // Update success state after all calls complete
                    this.setResultDisplay(testId, {
                        status: 'success',
                        message: `成功发送 ${times} 个请求`,
                        timestamp: this.formatTime(new Date()),
                        responseTime: '批量',
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
                        rt: 0
                    }

                    this.setResultDisplay(testId, {
                        status: 'error',
                        message: `批量请求失败: ${error.message}`,
                        timestamp: this.formatTime(new Date()),
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
                timestamp: this.formatTime(new Date())
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
                    rt: '并发'
                }

                // Update success state
                this.setResultDisplay(testId, {
                    status: 'success',
                    message: `成功完成 ${this.threadTimes} 个并发线程测试`,
                    timestamp: this.formatTime(new Date()),
                    responseTime: '并发',
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
                    rt: 0
                }

                this.setResultDisplay(testId, {
                    status: 'error',
                    message: `并发测试失败: ${error.message}`,
                    timestamp: this.formatTime(new Date()),
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
                timestamp: this.formatTime(new Date())
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
                    data: result
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
                    code: error.response?.status || 500
                });
                
                // 添加到历史记录
                const item = {
                    id: Date.now() + Math.random(),
                    time: this.formatTime(new Date()),
                    type: 'Dubbo服务端并发控制',
                    status: '失败',
                    code: error.response?.status || 500,
                    msg: error.response?.data?.message || error.message,
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
                    timestamp: this.formatTime(new Date())
                });
            } catch (error) {
                // 处理错误
                this.setResultDisplay('dubbo-actives', {
                    status: 'error',
                    message: `消费端并发控制测试失败: ${error.message}`,
                    timestamp: this.formatTime(new Date())
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
                default:
                    url = `/api/order/dubbo/version-group/v1-default?name=${encodeURIComponent(this.dubboVersionGroupName)}`;
            }
            await this.callWithResultDisplay(url, `dubbo-version-group-${this.dubboVersionGroupType}`, 'dubbo-version-group')
        },
        async testFeign() {
            await this.callWithResultDisplay(this.endpoint('feign'), 'feign', 'sca-feign')
        },
        async testLoadBalance() { await this.callWithResultDisplay(this.endpoint('load-balance'), 'load-balance', 'load-balance') },
        async testAsync() { await this.callWithResultDisplay(this.endpoint('async'), 'async', 'async') },
        async testTracing() { await this.callWithResultDisplay(this.endpoint('tracing'), 'tracing', 'tracing') },
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
                    rt: 0
                })
            }
            this.updateStatistics()
        },
        viewDetail(item) {
            this.detailView = JSON.stringify(item, null, 2)
            this.showDetailModal = true
            this.$nextTick(() => {
                this.highlightCode()
            })
        },
        showResultDetail(testId) {
            const result = this.resultDisplays[testId]
            if (result && result.data) {
                this.detailView = JSON.stringify(result.data, null, 2)
                this.showDetailModal = true
                this.$nextTick(() => {
                    this.highlightCode()
                })
            } else if (result && result.rawData) {
                this.detailView = JSON.stringify(result.rawData, null, 2)
                this.showDetailModal = true
                this.$nextTick(() => {
                    this.highlightCode()
                })
            } else if (result && result.message) {
                const detailInfo = {
                    testId: testId,
                    status: result.status,
                    message: result.message,
                    timestamp: result.timestamp,
                    responseTime: result.responseTime,
                    code: result.code
                }
                this.detailView = JSON.stringify(detailInfo, null, 2)
                this.showDetailModal = true
                this.$nextTick(() => {
                    this.highlightCode()
                })
            }
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
                timestamp: this.formatTime(new Date())
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
                    rt
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
                    rt: 0
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
                    rawData: errorItem  // 存储完整的错误数据对象，用于详情显示
                })
            }

            this.updateStatistics()
        },
        getApiInfo() {
            const apis = {
                sentinel: { method: 'GET', path: '/api/order/demo/flow-control', params: 'qps', description: '测试 Sentinel 的 QPS 限流规则，限制每秒最多 1000 个请求' },
                nacos: { method: 'GET', path: '/api/order/demo/nacos/services', params: '', description: '查询 Nacos 中注册的所有服务实例' },
                dubbo: { method: 'GET', path: '/api/order/dubbo/call-sync?productId=1', params: 'productId', description: '使用 Dubbo 协议远程调用 Product 服务' },
                seata: { method: 'POST', path: '/api/order/seata/tcc/commit', params: 'commodity, count', description: '执行 Seata TCC 分布式事务，涉及库存和账户的一致性保证' },
                higress: { method: 'GET', path: '/api/order/demo/gateway-routing', params: '', description: '验证网关路由规则配置是否生效' },
                sca: { method: 'GET', path: '/api/order/feign/product/1', params: 'productId', description: '使用 OpenFeign + LoadBalancer 调用 Product 服务' },
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
        updateStatistics() {
            const total = this.history.length
            const success = this.history.filter(h => h.status === '成功').length
            this.successRate = total > 0 ? Math.round((success / total) * 100) : 0
            const rtList = this.history.filter(h => h.rt > 0).map(h => h.rt)
            this.avgResponseTime = rtList.length > 0 ? Math.round(rtList.reduce((a, b) => a + b, 0) / rtList.length) : 0
            const testedSet = new Set(this.history.map(h => h.type))
            this.testedComponents = testedSet.size
        },
        async testAll() {
            const comp = this.activeComponent
            const tests = {
                sentinel: ['testQps', 'testThread', 'testHotspot', 'testDegrade'],
                nacos: ['testNacosServices', 'testNacosConfig'],
                dubbo: ['testDubboSync', 'testDubboBatch', 'testDubboListAll', 'testDubboTimeout', 'testDubboException', 'testDubboAsync', 'testDubboRegion', 'testDubboConcurrency', 'testDubboActives', 'testDubboLeastActive'],
                seata: ['testTccOk', 'testTccFail'],
                sca: ['testFeignVsDubbo', 'testLoadBalance', 'testAsync'],
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
        }
    },
    mounted() {
        // 初始化主题
        this.applyTheme();
        
        // 初始化侧边栏宽度 - 延迟执行确保 DOM 完全渲染
        this.$nextTick(() => {
            this.updateSidebarWidth();
        });
        
        // 根据当前选中的组件展开对应的菜单组
        this.updateActivePanoramaTab(this.activeComponent);
        
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
