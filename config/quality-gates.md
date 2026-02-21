# Quality Gates

## CI Pipeline

Workflow: `.github/workflows/ci-baseline.yml`

Stages:

1. `build`
- Command: `mvn -q -DskipTests validate`
- Gate: must pass Maven Enforcer (Java 21, Maven 3.9+) and validation lifecycle.

2. `test`
- Commands:
  - `mvn -q -pl services/service-order -am "-Dtest=OrderControllerRocketMqTest,ScenarioControlControllerTest,CoreScenarioEvidenceContractTest,OrderDemoFacadeGuidedFlowTest,RocketMqDemoFacadeTest" "-Dsurefire.failIfNoSpecifiedTests=false" test`
  - `mvn -q -pl services/service-order-dubbo -am "-Dtest=OrderDubboControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test`
  - `mvn -q -pl services/seata-business -am "-Dtest=PurchaseRestControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test`
- Gate: deterministic interview-stage test suite must pass (no external MQ runtime dependency).

3. `smoke`
- Commands:
  - `scripts/validate-demo-contract.ps1`
  - `scripts/validate-scenario-catalog.ps1`
  - `scripts/validate-core-stage.ps1`
- Gate: contract and catalog smoke checks must pass.

## Local Repro Commands

Use the same command set locally:

```powershell
mvn -q -DskipTests validate
mvn -q -pl services/service-order -am "-Dtest=OrderControllerRocketMqTest,ScenarioControlControllerTest,CoreScenarioEvidenceContractTest,OrderDemoFacadeGuidedFlowTest,RocketMqDemoFacadeTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -q -pl services/service-order-dubbo -am "-Dtest=OrderDubboControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -q -pl services/seata-business -am "-Dtest=PurchaseRestControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-scenario-catalog.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1
```

Or use wrappers where applicable:

- `scripts/build-validate.ps1`
- `scripts/build-validate.sh`
- `scripts/run-interview-kit.ps1` (requires local services started)
