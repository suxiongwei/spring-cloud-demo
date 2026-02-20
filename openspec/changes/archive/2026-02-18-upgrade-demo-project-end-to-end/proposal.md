## Why

当前项目已经覆盖了较多微服务技术点，但“可运行一致性、场景真实性、工程化闭环”还不够统一，导致演示稳定性和面试说服力受限。需要一次性完成端到端升级，把“能展示”提升为“可复现、可验证、可维护”的高级工程样例。

## What Changes

- 建立统一场景目录与成熟度分层（implemented/demo-only/planned），并将页面展示、后端实现、验证方式对齐。
- 统一前后端接口契约，修复静态控制台与实际后端接口漂移，避免“按钮可见但接口不可用”。
- 推进环境可移植化：配置参数化（地址、namespace、端口、凭证）、本地 profile、`.env.example`、一键环境启动。
- 强化构建一致性：统一 Java/Maven 版本约束，避免编译运行时环境不一致。
- 拆分高复杂度大类（OrderController/RedisTestController/ProductDubboClient）并下沉业务编排逻辑，提高可维护性。
- 统一公共响应模型与错误码，消除跨模块重复定义。
- 增加多层测试体系：单元测试、集成测试、冒烟脚本，覆盖 Gateway/Sentinel/Dubbo/Seata/Redis 主路径。
- 完成 Seata 成功/回滚可验证闭环，提供事务前后状态对比与失败注入路径。
- 为 Sentinel、Dubbo、Redis 场景补齐标准化压测/验证脚本与预期结果，形成“可讲可证据化”能力。
- 增加 CI 基线（至少 build + test + smoke）并输出统一验收标准。
- 新增“场景编目与演示模式”入口，支持按链路顺序执行核心实验。
- 补齐高级话题最小闭环：先落一个真实 OpenTelemetry 链路追踪贯通样例；对 RocketMQ/Chaos/Higress/OpenSergo/SchedulerX/AppActive 明确分层（已实现或规划）。

## Capabilities

### New Capabilities

- `scenario-catalog-and-maturity`: 建立场景目录、成熟度标注、依赖与验证步骤统一治理。
- `frontend-backend-contract-alignment`: 前端配置与后端接口的单一事实源管理与一致性校验。
- `portable-runtime-configuration`: 环境变量化配置、本地默认 profile、跨机器可运行能力。
- `build-and-toolchain-consistency`: Java/Maven 版本约束与构建前置校验。
- `controller-and-client-modularization`: 大类拆分与分层重构（controller/facade/service）。
- `shared-response-model-standardization`: 统一 ApiResponse/ResultCode 等公共协议模型。
- `multi-layer-test-system`: 单测、集成、冒烟脚本分层测试体系与覆盖策略。
- `seata-verifiable-transaction-scenarios`: Seata AT/TCC 成功与回滚的状态可视化与可重复验证。
- `sentinel-dubbo-redis-benchmark-kits`: 关键治理与通信场景的标准测试输入、脚本和验收结果。
- `ci-baseline-and-quality-gates`: 最小 CI 流水线与质量门禁。
- `guided-interview-demo-flow`: 面试模式演示编排（发现->调用->限流->事务->回滚）。
- `minimal-observability-trace-closure`: OpenTelemetry 最小链路追踪闭环与展示。
- `advanced-topic-implementation-statusing`: RocketMQ/Chaos/Higress/OpenSergo/SchedulerX/AppActive 的实现状态分层与路线图。

### Modified Capabilities

- None

## Impact

- Affected code:
  - `gateway/src/main/resources/static/**`（场景配置、接口映射、演示流程）
  - `gateway/src/main/resources/application*.y*ml`（网关运行与配置参数化）
  - `services/service-order/**`（Sentinel/Feign/场景编排与测试）
  - `services/service-order-dubbo/**`（Dubbo/Redis 场景拆分与测试）
  - `services/service-product-dubbo/**`（Dubbo 对齐与验证）
  - `services/seata-*/**`（AT/TCC 场景与状态验证）
  - `model/**`（公共响应模型下沉）
  - `pom.xml`, `services/pom.xml`, 模块 `pom.xml`（构建约束、测试与 CI 支撑）
- Affected APIs: Gateway 暴露的演示接口、order/order-dubbo/seata 相关演示与验证接口。
- Affected dependencies/systems: Nacos、Sentinel、Redis、Seata、Dubbo；新增/完善 docker-compose、smoke 脚本与 CI 执行环境。
- Delivery impact: 该 change 覆盖面较大，将分阶段交付（先可运行一致性，再场景证据化，最后工程化与高级闭环）。
