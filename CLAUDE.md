# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Cloud Alibaba microservices demo project implementing distributed service architecture with:
- **Spring Boot 3.3.4** on **JDK 21**
- **Spring Cloud 2023.0.3** with **Alibaba 2023.0.3.2**
- **Dubbo 3.2.15** for RPC
- **Nacos** for service discovery and configuration center

## Build Commands

### Compile entire project
```bash
mvn clean compile -DskipTests
```

### Build and package
```bash
mvn clean package -DskipTests
```

### Run specific service (example)
```bash
cd services/service-product
mvn spring-boot:run
```

### Run tests for specific service
```bash
cd services/service-order
mvn test
```

### Run a single test class
```bash
cd services/service-order
mvn test -Dtest=OrderControllerTest
```

### Check code coverage
```bash
mvn clean verify
# Coverage report: target/site/jacoco/index.html
```

## Architecture

### Module Structure

```
spring-cloud-demo/
├── model/                    # Shared domain models
├── gateway/                  # API Gateway (Spring Cloud Gateway)
└── services/                 # All microservices
    ├── service-product       # Product service (REST/OpenFeign)
    ├── service-product-dubbo # Product service (Dubbo RPC)
    ├── service-order         # Order service (REST/OpenFeign)
    ├── service-order-dubbo   # Order service (Dubbo RPC)
    ├── seata-account         # Seata distributed transaction demo
    ├── seata-business
    ├── seata-order
    └── seata-storage
```

### Key Technology Stack

- **Service Discovery**: Nacos (`spring-cloud-starter-alibaba-nacos-discovery`)
- **Configuration Center**: Nacos (`spring-cloud-starter-alibaba-nacos-config`)
- **API Gateway**: Spring Cloud Gateway
- **Remote Call**: OpenFeign + Dubbo
- **Load Balancing**: Spring Cloud LoadBalancer (client-side)
- **Circuit Breaker**: Sentinel (`spring-cloud-starter-alibaba-sentinel`)
- **Distributed Transactions**: Seata (`spring-cloud-starter-alibaba-seata`)
- **Testing**: JUnit5, Spring Boot Test, AssertJ, Json-Unit
- **Code Quality**: JaCoCo (70% minimum coverage requirement)

### Service Communication Patterns

1. **REST + OpenFeign** (service-product/service-order)
   - REST controllers expose HTTP endpoints
   - OpenFeign clients call other services by service name
   - Load balanced via `@LoadBalanced` RestTemplate

2. **Dubbo RPC** (service-product-dubbo/service-order-dubbo)
   - Dubbo services with `@DubboService`
   - Dubbo clients with `@DubboReference`
   - Nacos as registry and config center
   - Method-level timeout configuration support

3. **API Gateway Routing**
   - `/api/order/**` → service-order
   - `/api/product/**` → service-product
   - Path rewrite: `/api/order/x` → `/x` (service receives clean path)

### Configuration Management

- **Namespace**: `default` (Nacos data isolation)
- **Group**: `SERVICE_GROUP` for service configs
- **Data ID**: `service-{name}.yaml` pattern
- **Profile-specific**: `service-name-{profile}.yaml` for environment configs
- **Dynamic Refresh**: `@RefreshScope` + `@ConfigurationProperties`

### Enterprise Sentinel Integration

The `service-order` module implements advanced Sentinel strategies:
- Multiple rule types configured via Nacos (JSON format)
- Flow control, degrade, hotspot, system rules
- See `service-order/sentinel-nacos-config.md` for detailed configuration
- Example rules in `nacos-sentinel-rules-examples.json`

### Testing Infrastructure

- **HTTP Testing**: ATT/http/requests.http or import `ATT/postman/collection.json`
- **Gateway Routes**: `/api/order/**`, `/api/product/**`
- **Authentication**: Header `X-Token` or JWT: `Authorization: Bearer <token>`
- **Sentinel Testing**: Use `/order/degrade/rt` (RT threshold) or `/order/rateLimit/qps`
- **Transaction Consistency**: Test via `/purchase?userId=u1&commodityCode=c1&count=1`

## Common Development Tasks

### Adding New Service
1. Create Maven module under `services/`
2. Add to `services/pom.xml` `<modules>`
3. Inherit from `services/pom.xml` parent
4. Add main class with `@SpringBootApplication` + `@EnableDiscoveryClient`
5. Configure `application.yml` with Nacos server address
6. Add module dependencies through parent's dependencyManagement

### Running All Services
```bash
# Terminal 1: Nacos (standalone mode)
cd /path/to/nacos/bin
startup.cmd -m standalone

# Terminal 2: Seata
cd /path/to/seata/bin
seata-server.bat

# Terminal 3: Gateway
cd gateway
mvn spring-boot:run

# Terminal 4: Product Service
cd services/service-product
mvn spring-boot:run

# Terminal 5: Order Service
cd services/service-order
mvn spring-boot:run
```

### Nacos Configuration Updates
Use provided PowerShell scripts in root:
- `publish-app-config.ps1` - Publish application configuration
- `publish-method-config.ps1` - Publish Dubbo method-level config
- `check-nacos-config.ps1` - Verify Nacos configuration
- `check-dubbo-config-center.ps1` - Check Dubbo config center
- `test-dubbo-listener.ps1` - Test Dubbo listener configuration

### Package & Deploy
```bash
# Package all services
mvn clean package -DskipTests

# Individual service JARs will be in:
# services/{service-name}/target/*.jar
```

## Critical Configuration Requirements

- **JDK Version**: 21 (configured in parent POM)
- **Nacos Address**: `172.29.64.1:8848` (check `application.yml` files)
- **Seata Address**: `172.29.64.1:8091`
- **Minimum Coverage**: 70% (enforced by JaCoCo)
- **Ports**: Avoid conflicts (default: gateway:8888, order:8000, product:9000)
