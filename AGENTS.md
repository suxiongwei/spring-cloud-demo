# Repository Guidelines

## Project Structure & Module Organization
- 根目录是 Maven 多模块项目，`pom.xml` 聚合 `gateway/`、`services/`、`model/`。
- `gateway/`：Spring Cloud Gateway 与静态演示页面（`src/main/resources/static/`）。
- `services/`：业务微服务与 Seata 模块（如 `service-order/`、`service-product/`、`seata-*`）。
- `model/`：共享 DTO/接口定义。
- `ATT/` 与 `docs/`：环境、架构、测试文档与示例素材；接口测试文件在 `ATT/http/requests.http`。

## Build, Test, and Development Commands
- `mvn -q -DskipTests package`：构建全部模块（默认 JDK 21，README 建议 JDK 17 也可）。
- `mvn -pl gateway -am spring-boot:run`：启动网关。
- `mvn -pl services/service-order -am spring-boot:run`：启动订单服务示例。
- 依赖环境：`docker run -p 8848:8848 nacos/nacos-server:latest`，`docker run -p 8080:8080 bladex/sentinel-dashboard`（详见 `ATT/docs/env-setup.md`）。

## Coding Style & Naming Conventions
- Java 使用 4 空格缩进，包名全小写（如 `indi.mofan.*`），类名 PascalCase，变量/方法 camelCase。
- Spring Boot 标准命名：`application.yml`、`logback-spring.xml` 放在 `src/main/resources/`。
- 共享模型放在 `model/`，跨服务调用接口放在 `services/**/feign` 或 Dubbo 接口包。

## Testing Guidelines
- 使用 `spring-boot-starter-test`（JUnit 5）进行单测；测试类放在 `src/test/java`，命名以 `*Test` 结尾。
- Jacoco 在根 `pom.xml` 里配置，`mvn test` 生成覆盖率报告，`mvn verify` 强制行覆盖率 ≥ 70%。
- 接口与场景测试参考 `ATT/docs/tests.md`（含熔断、流控、Seata 入口）。

## Commit & Pull Request Guidelines
- 历史提交多采用 Conventional Commits：`feat:`、`refactor:`、`style:`，可带范围如 `feat(ui):`。
- PR 请包含：变更摘要、影响模块、验证方式（命令/截图/日志）；涉及 UI 变更请附截图；若关联问题请链接 issue。

## Security & Configuration Tips
- 配置以 `application.yml` 为主，敏感信息用环境变量覆盖；`spring.config.import` 中的环境隔离配置保持与 Nacos Namespace 对齐。
