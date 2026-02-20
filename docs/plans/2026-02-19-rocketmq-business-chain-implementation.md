# RocketMQ Business Chain Scenarios Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Land 10 RocketMQ business-chain demo scenarios with runnable endpoints, frontend triggers, and tests so developers can learn usage and principles end-to-end.

**Architecture:** Add a dedicated RocketMQ demo facade in `service-order` with an in-memory scenario engine that models enterprise RocketMQ behavior (topic/tag/key, retry, DLQ, idempotency, ordering, delay, transaction check, replay). Expose endpoints under `/demo/rocketmq/*` and wire them into gateway frontend scenario panels. Keep contract files in sync.

**Tech Stack:** Spring Boot 3, existing `ApiResponse` contract, Vue static demo page, JUnit5 + MockMvc.

---

### Task 1: Add failing tests for RocketMQ backend API contract

**Files:**
- Create: `services/service-order/src/test/java/indi/mofan/order/RocketMqDemoFacadeTest.java`
- Create: `services/service-order/src/test/java/indi/mofan/order/controller/OrderControllerRocketMqTest.java`

**Step 1: Write failing tests for 10 scenario methods and key response fields**

**Step 2: Run tests to verify failures**

Run: `mvn -pl services/service-order -Dtest=RocketMqDemoFacadeTest,OrderControllerRocketMqTest test`
Expected: FAIL due to missing facade/controller methods.

**Step 3: Implement minimal methods to compile**

**Step 4: Run tests to verify pass**

Run: `mvn -pl services/service-order -Dtest=RocketMqDemoFacadeTest,OrderControllerRocketMqTest test`
Expected: PASS.

**Step 5: Commit**

Run:
```bash
git add services/service-order/src/test/java/indi/mofan/order/RocketMqDemoFacadeTest.java services/service-order/src/test/java/indi/mofan/order/controller/OrderControllerRocketMqTest.java
git commit -m "test: add RocketMQ business-chain scenario API tests"
```

### Task 2: Implement RocketMQ scenario facade and controller endpoints

**Files:**
- Create: `services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java`
- Modify: `services/service-order/src/main/java/indi/mofan/order/controller/OrderController.java`

**Step 1: Write failing assertions for scenario-specific payload structure**

**Step 2: Run tests to confirm failure reason is missing behavior**

**Step 3: Implement 10 scenario methods**

- `publish-basic`
- `retry`
- `dlq`
- `idempotent`
- `orderly`
- `delay-close`
- `tx/send`
- `tx/check`
- `tag-filter`
- `replay-dlq`

Each payload includes: `scenarioId`, `businessContext`, `topic`, `messageKey`, `principle`, and scenario evidence fields.

**Step 4: Run backend tests**

Run: `mvn -pl services/service-order -Dtest=RocketMqDemoFacadeTest,OrderControllerRocketMqTest,OrderControllerTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java services/service-order/src/main/java/indi/mofan/order/controller/OrderController.java
git commit -m "feat: add 10 RocketMQ business-chain demo endpoints"
```

### Task 3: Wire frontend RocketMQ scenario panels and endpoint mapping

**Files:**
- Modify: `gateway/src/main/resources/static/service-demo-redesign.html`
- Modify: `gateway/src/main/resources/static/js/service-demo.js`

**Step 1: Add failing frontend mapping checks (manual smoke list in plan execution)**

**Step 2: Implement endpoint map and test handlers**

Add endpoint keys for all 10 scenarios in `endpoint()` and runnable methods in Vue app.

**Step 3: Add UI sections for RocketMQ business-chain scenarios**

Add navigation subitems + control groups with one-click run buttons and result display ids.

**Step 4: Manual verification**

Open demo page and verify 10 RocketMQ scenario buttons trigger `/api/order/demo/rocketmq/*` calls.

**Step 5: Commit**

```bash
git add gateway/src/main/resources/static/service-demo-redesign.html gateway/src/main/resources/static/js/service-demo.js
git commit -m "feat: add RocketMQ business-chain scenarios in demo UI"
```

### Task 4: Update contract and catalog metadata

**Files:**
- Modify: `config/scenario-catalog.json`
- Modify: `config/demo-endpoint-inventory.md`

**Step 1: Add failing contract expectation (script/manual diff check)**

**Step 2: Update allowed endpoint patterns for RocketMQ 10 routes**

**Step 3: Update inventory doc canonical frontend contract section**

**Step 4: Run contract validation script**

Run: `powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1`
Expected: PASS.

**Step 5: Commit**

```bash
git add config/scenario-catalog.json config/demo-endpoint-inventory.md
git commit -m "chore: register RocketMQ scenario contract and inventory"
```

### Task 5: Full verification

**Files:**
- Verify only.

**Step 1: Run focused module tests**

Run: `mvn -pl services/service-order -Dtest=RocketMqDemoFacadeTest,OrderControllerRocketMqTest,OrderControllerTest test`

**Step 2: Run gateway static contract validation**

Run: `powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1`

**Step 3: Run optional broader tests if time allows**

Run: `mvn -pl services/service-order,gatew` + `ay test` (split commands per module in actual run).

**Step 4: Verify no unintended file changes**

Run: `git status --short`
Expected: only intended files changed.

**Step 5: Prepare final summary with evidence**

---

## Real RocketMQ Runbook (2026-02-19)

1. Start broker stack:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-rocketmq.ps1 -BrokerIp1 127.0.0.1
```

2. Run order service with real broker:

```powershell
$env:ROCKETMQ_NAME_SERVER="127.0.0.1:9876"
```

3. Smoke two key scenarios:

```http
GET /api/order/demo/rocketmq/publish-basic
GET /api/order/demo/rocketmq/tx/check
```

4. Stop broker stack:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/stop-rocketmq.ps1
```
