# Interview Demo Stage Upgrade Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an interview-ready demo stage that focuses on 4 business chains with deterministic evidence and one-command report generation.

**Architecture:** Keep only mature capabilities on the main stage, move roadmap capabilities to secondary display, and standardize core scenario output using shared evidence fields. Upgrade guided flow metadata to executable contract and add a script runner that executes key endpoints and exports a markdown report for interview rehearsal.

**Tech Stack:** Java 21, Spring Boot 3.3.x, Spring Cloud Alibaba, Dubbo, Seata, RocketMQ, Redis, Vue static page, PowerShell scripts, JUnit5 + MockMvc.

---

## Skill Constraints

- Use `@superpowers/test-driven-development` for each backend change.
- Use `@superpowers/verification-before-completion` before claiming task completion.
- Keep commits small and task-scoped.

### Task 1: Core Stage Partition (Main vs Roadmap)

**Files:**
- Modify: `config/scenario-catalog.json`
- Modify: `gateway/src/main/resources/static/js/service-demo.js`
- Modify: `gateway/src/main/resources/static/js/config/services-config.js`
- Modify: `gateway/src/main/resources/static/service-demo-redesign.html`
- Create: `scripts/validate-core-stage.ps1`
- Test: `scripts/validate-core-stage.ps1`

**Step 1: Write failing validation script**

```powershell
# scripts/validate-core-stage.ps1
$core = @('sentinel','nacos','gateway','sca','dubbo','redis','seata','rocketmq')
# Fail if any core capability is not implemented.
```

**Step 2: Run validation to verify it fails**

Run: `powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1`
Expected: FAIL because script exists but catalog/UI are not aligned yet.

**Step 3: Implement minimal catalog and UI partition**

```json
{
  "ui": {
    "mainStageIds": ["sentinel","nacos","gateway","sca","dubbo","redis","seata","rocketmq"],
    "roadmapStageIds": ["higress","opentelemetry","k8s","opensergo","chaosblade","appactive","schedulerx","arctic"]
  }
}
```

**Step 4: Run validation to verify it passes**

Run: `powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1`
Expected: PASS with summary count for main and roadmap stage.

**Step 5: Commit**

```bash
git add config/scenario-catalog.json gateway/src/main/resources/static/js/service-demo.js gateway/src/main/resources/static/js/config/services-config.js gateway/src/main/resources/static/service-demo-redesign.html scripts/validate-core-stage.ps1
git commit -m "feat: partition demo capabilities into core stage and roadmap"
```

### Task 2: Guided Flow v2 Contract and API Response

**Files:**
- Modify: `services/service-order/src/main/java/indi/mofan/order/facade/OrderDemoFacade.java`
- Modify: `services/service-order/src/main/java/indi/mofan/order/controller/OrderController.java`
- Create: `services/service-order/src/test/java/indi/mofan/order/controller/OrderControllerGuidedFlowTest.java`
- Test: `services/service-order/src/test/java/indi/mofan/order/controller/OrderControllerGuidedFlowTest.java`

**Step 1: Write failing controller test**

```java
@Test
void guidedFlowShouldExposeExecutableAssertions() throws Exception {
    mockMvc.perform(get("/demo/guided-flow"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.flowName").value("java-senior-interview-guided-flow-v2"))
        .andExpect(jsonPath("$.data.steps[0].assertion.rule").exists())
        .andExpect(jsonPath("$.data.runId").exists());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl services/service-order -Dtest=OrderControllerGuidedFlowTest test`
Expected: FAIL because current payload has no `runId` and no structured assertion object.

**Step 3: Implement minimal API changes**

```java
result.put("flowName", "java-senior-interview-guided-flow-v2");
result.put("runId", UUID.randomUUID().toString());
result.put("steps", stepsWithAssertionObject);
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl services/service-order -Dtest=OrderControllerGuidedFlowTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/service-order/src/main/java/indi/mofan/order/facade/OrderDemoFacade.java services/service-order/src/main/java/indi/mofan/order/controller/OrderController.java services/service-order/src/test/java/indi/mofan/order/controller/OrderControllerGuidedFlowTest.java
git commit -m "feat: upgrade guided flow to executable v2 contract"
```

### Task 3: Standardize Scenario Evidence Fields in Core Endpoints

**Files:**
- Create: `model/src/main/java/indi/mofan/common/demo/ScenarioEvidenceKeys.java`
- Modify: `services/service-order/src/main/java/indi/mofan/order/facade/OrderDemoFacade.java`
- Modify: `services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java`
- Modify: `services/service-order-dubbo/src/main/java/indi/mofan/order/controller/OrderDubboController.java`
- Create: `services/service-order/src/test/java/indi/mofan/order/controller/CoreScenarioEvidenceContractTest.java`
- Test: `services/service-order/src/test/java/indi/mofan/order/controller/CoreScenarioEvidenceContractTest.java`

**Step 1: Write failing evidence contract test**

```java
@Test
void coreScenarioResponseShouldContainEvidenceFields() throws Exception {
    mockMvc.perform(get("/demo/gateway-routing"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.scenarioId").exists())
        .andExpect(jsonPath("$.data.success").exists())
        .andExpect(jsonPath("$.data.evidence").exists())
        .andExpect(jsonPath("$.data.costMs").exists());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl services/service-order -Dtest=CoreScenarioEvidenceContractTest test`
Expected: FAIL on missing fields.

**Step 3: Implement shared response fields**

```java
result.put("scenarioId", "gateway-routing");
result.put("success", true);
result.put("failureInjected", false);
result.put("costMs", elapsed);
result.put("evidence", Map.of("routeCount", routeCount));
result.put("nextQuestionHints", List.of("如何做灰度发布?", "网关限流放在哪一层?"));
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl services/service-order -Dtest=CoreScenarioEvidenceContractTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add model/src/main/java/indi/mofan/common/demo/ScenarioEvidenceKeys.java services/service-order/src/main/java/indi/mofan/order/facade/OrderDemoFacade.java services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java services/service-order-dubbo/src/main/java/indi/mofan/order/controller/OrderDubboController.java services/service-order/src/test/java/indi/mofan/order/controller/CoreScenarioEvidenceContractTest.java
git commit -m "feat: standardize evidence fields for core interview scenarios"
```

### Task 4: One-Command Interview Kit Runner

**Files:**
- Create: `scripts/run-interview-kit.ps1`
- Modify: `scripts/smoke-core.ps1`
- Create: `reports/.gitkeep`
- Modify: `config/build-commands.md`
- Test: `scripts/run-interview-kit.ps1`

**Step 1: Write failing runner skeleton**

```powershell
param([string]$BaseUrl = "http://localhost:9090")
throw "Not implemented"
```

**Step 2: Run script to verify it fails**

Run: `powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1`
Expected: FAIL with `Not implemented`.

**Step 3: Implement endpoint execution + markdown report**

```powershell
$cases = @(
  @{name='chain-a-gateway'; path='/api/order/demo/gateway-routing'},
  @{name='chain-b-dubbo'; path='/api/order/dubbo/call-sync?productId=1'},
  @{name='chain-c-seata'; path='/api/business/purchase/tcc/verify?userId=U1001&commodityCode=P0001&count=1&fail=false'},
  @{name='chain-d-rocketmq'; path='/api/order/demo/rocketmq/publish-basic'}
)
# invoke and write reports/interview-kit-report.md
```

**Step 4: Run script to verify it passes**

Run: `powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1 -BaseUrl http://localhost:9090`
Expected: PASS when services are up; generates `reports/interview-kit-report.md`.

**Step 5: Commit**

```bash
git add scripts/run-interview-kit.ps1 scripts/smoke-core.ps1 reports/.gitkeep config/build-commands.md
git commit -m "feat: add one-command interview kit runner and report export"
```

### Task 5: Deterministic Reset and Failure Injection

**Files:**
- Create: `services/service-order/src/main/java/indi/mofan/order/controller/ScenarioControlController.java`
- Modify: `services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java`
- Modify: `services/seata-business/src/main/java/indi/mofan/business/controller/PurchaseRestController.java`
- Modify: `services/service-order-dubbo/src/main/java/indi/mofan/order/controller/redis/RedisScenarioController.java`
- Create: `services/service-order/src/test/java/indi/mofan/order/controller/ScenarioControlControllerTest.java`
- Test: `services/service-order/src/test/java/indi/mofan/order/controller/ScenarioControlControllerTest.java`

**Step 1: Write failing reset/fail-injection tests**

```java
@Test
void shouldResetDemoState() throws Exception {
    mockMvc.perform(post("/demo/control/reset"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.resetApplied").value(true));
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl services/service-order -Dtest=ScenarioControlControllerTest test`
Expected: FAIL (missing controller/endpoint).

**Step 3: Implement reset + injection toggles**

```java
@PostMapping("/demo/control/reset")
public ApiResponse<Object> resetState() { ... }

@PostMapping("/demo/control/failure-injection")
public ApiResponse<Object> setFailureInjection(...) { ... }
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl services/service-order -Dtest=ScenarioControlControllerTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add services/service-order/src/main/java/indi/mofan/order/controller/ScenarioControlController.java services/service-order/src/main/java/indi/mofan/order/facade/RocketMqDemoFacade.java services/seata-business/src/main/java/indi/mofan/business/controller/PurchaseRestController.java services/service-order-dubbo/src/main/java/indi/mofan/order/controller/redis/RedisScenarioController.java services/service-order/src/test/java/indi/mofan/order/controller/ScenarioControlControllerTest.java
git commit -m "feat: add deterministic reset and failure injection controls"
```

### Task 6: CI and Quality Gates for Interview Stage

**Files:**
- Modify: `.github/workflows/ci-baseline.yml`
- Modify: `config/quality-gates.md`
- Modify: `scripts/build-validate.ps1`
- Modify: `scripts/build-validate.sh`
- Test: CI-equivalent local command set

**Step 1: Write expected command sequence in docs first**

```markdown
- powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1
- powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1
```

**Step 2: Run local sequence to verify current behavior**

Run:
`mvn -q -DskipTests validate`
`mvn -q -pl services/service-order,services/service-order-dubbo,services/seata-business -am test`
`powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1`
`powershell -ExecutionPolicy Bypass -File scripts/validate-scenario-catalog.ps1`
Expected: Identify missing gates before CI update.

**Step 3: Update CI and wrappers**

```yaml
- name: Validate core stage
  run: powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1
```

**Step 4: Re-run local verification**

Run: same command set + `scripts/run-interview-kit.ps1` (when local env is ready)
Expected: PASS, or explicit environment dependency note.

**Step 5: Commit**

```bash
git add .github/workflows/ci-baseline.yml config/quality-gates.md scripts/build-validate.ps1 scripts/build-validate.sh
git commit -m "chore: enforce interview-stage quality gates in CI"
```

### Task 7: Interview Playbook and Q&A Hints

**Files:**
- Create: `docs/interview/playbook-30min.md`
- Modify: `config/scenario-kits/dubbo-kit.md`
- Modify: `config/scenario-kits/redis-kit.md`
- Modify: `config/scenario-kits/sentinel-kit.md`
- Create: `config/scenario-kits/seata-kit.md`
- Create: `config/scenario-kits/rocketmq-kit.md`
- Test: Manual dry-run with generated interview report

**Step 1: Write minimal failing checklist (manual criteria)**

```markdown
- Must finish 4 chains within 30 minutes.
- Every chain must include one failure-path explanation.
```

**Step 2: Dry-run current state and record gaps**

Run: `powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1`
Expected: Identify missing hints or unclear storyline.

**Step 3: Implement playbook and scenario-kit Q&A sections**

```markdown
## Interview Drill-down
Q: Why Dubbo over Feign here?
A: Internal high-throughput RPC, binary protocol, and richer governance knobs.
```

**Step 4: Re-run dry-run and verify readability**

Run: `powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1`
Expected: Report + playbook can drive a complete walkthrough.

**Step 5: Commit**

```bash
git add docs/interview/playbook-30min.md config/scenario-kits/dubbo-kit.md config/scenario-kits/redis-kit.md config/scenario-kits/sentinel-kit.md config/scenario-kits/seata-kit.md config/scenario-kits/rocketmq-kit.md
git commit -m "docs: add 30-minute interview playbook and scenario Q&A hints"
```

## Final Verification (Before Merge)

Run:

```bash
mvn -q -DskipTests validate
mvn -q -pl services/service-order,services/service-order-dubbo,services/seata-business -am test
powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-scenario-catalog.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1
```

If local middleware environment is up, also run:

```bash
powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1 -BaseUrl http://localhost:9090
```
