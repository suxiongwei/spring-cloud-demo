# Real RocketMQ Mode Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Convert 10 RocketMQ business scenarios from simulated mode to strict real RocketMQ mode with real producer/consumer/transaction flow.

**Architecture:** Add RocketMQ runtime infrastructure in `service-order` using `rocketmq-spring-boot-starter`, with dedicated listeners and transaction listener writing evidence into runtime state. Scenario APIs publish real messages and block for bounded time until expected evidence arrives. Add docker compose for NameServer/Broker.

**Tech Stack:** Spring Boot 3.3, RocketMQ Spring Boot Starter, Docker Compose, JUnit5 + MockMvc.

---

### Task 1: Add failing tests for strict real mode facade behavior

**Files:**
- Modify: `services/service-order/src/test/java/indi/mofan/order/RocketMqDemoFacadeTest.java`
- Modify: `services/service-order/src/test/java/indi/mofan/order/controller/OrderControllerRocketMqTest.java`

**Step 1: Write failing tests**
- Verify facade returns failure when send throws exception.
- Keep 10 endpoint contract tests in controller.

**Step 2: Run test to verify fails**
Run:
`mvn -pl services/service-order -am "-Dtest=RocketMqDemoFacadeTest,OrderControllerRocketMqTest" "-Dsurefire.failIfNoSpecifiedTests=false" test`

**Step 3: Implement minimal code to pass**

**Step 4: Re-run tests**
Same command, expect PASS.

### Task 2: Implement real RocketMQ runtime components

**Files:**
- Modify: `services/service-order/pom.xml`
- Modify: `services/service-order/src/main/resources/application.yml`
- Modify: `services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java`
- Create: `services/service-order/src/main/java/indi/mofan/order/rocketmq/RocketMqScenarioRuntimeState.java`
- Create: `services/service-order/src/main/java/indi/mofan/order/rocketmq/RocketMqDemoMessage.java`
- Create: `services/service-order/src/main/java/indi/mofan/order/rocketmq/RocketMqDemoProducer.java`
- Create: `services/service-order/src/main/java/indi/mofan/order/rocketmq/listener/*.java`

**Step 1: Add dependency and config**
- Add `rocketmq-spring-boot-starter`.
- Add name-server, producer group, topics, timeout config.

**Step 2: Implement producer + message model**
- sync send
- orderly send
- delay send
- transaction send

**Step 3: Implement listeners + transaction listener**
- basic dual-group consumers
- retry consumer (first 2 fail then success)
- dlq trigger consumer + dlq observer
- idempotent/orderly/delay/tag consumers
- transaction listener for scenario 07/08

**Step 4: Integrate facade orchestration and strict timeout**
- each API publishes and waits for expected evidence
- timeout => fail

**Step 5: Run tests**
Run the command in Task 1.

### Task 3: Add local RocketMQ infra

**Files:**
- Create: `docker-compose.rocketmq.yml`
- Create: `deploy/rocketmq/broker.conf`
- Create: `scripts/start-rocketmq.ps1`
- Create: `scripts/stop-rocketmq.ps1`

**Step 1: Add NameServer + Broker compose stack**

**Step 2: Add broker tuning for demo**
- low transaction check interval
- brokerIP1 support

**Step 3: Add helper scripts**
- start/stop with health hint

**Step 4: Smoke instructions**
- call two APIs and verify response evidence

### Task 4: Verification and docs update

**Files:**
- Modify: `docs/plans/2026-02-19-rocketmq-business-chain-implementation.md`

**Step 1: Run targeted tests**
`mvn -pl services/service-order -am "-Dtest=RocketMqDemoFacadeTest,OrderControllerRocketMqTest" "-Dsurefire.failIfNoSpecifiedTests=false" test`

**Step 2: Run contract script**
`powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1`

**Step 3: Document run command**
- compose up/down and API verify examples.

