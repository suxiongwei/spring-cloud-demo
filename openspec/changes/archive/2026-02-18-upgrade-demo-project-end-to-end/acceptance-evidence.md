# Acceptance Evidence Summary

## Change

- Name: `upgrade-demo-project-end-to-end`
- Schema: `spec-driven`

## Completed Work Highlights

- Scenario catalog + maturity model established.
- Frontend/backend contract validation and legacy alias strategy implemented.
- Runtime configuration parameterized for Nacos/Sentinel/Redis/Seata and local defaults.
- Maven Enforcer added for Java/Maven toolchain gates.
- Seata TCC verification endpoint added with before/after snapshots and rollback/commit assertions.
- CI baseline workflow and quality gate documents added.

## Validation Evidence

### Contract Gate

- Command: `scripts/validate-demo-contract.ps1`
- Result: PASS

### Scenario Catalog Completeness Gate

- Command: `scripts/validate-scenario-catalog.ps1`
- Result: PASS

### Smoke Baseline

- Command: `scripts/smoke-core.ps1 -BaseUrl http://localhost:9090 -Output reports/smoke-core-report.json`
- Report: `reports/smoke-core-report.json`
- Result in current environment: `0/7 passed`
- Observation: gateway/service stack was not running; failures are connectivity failures and represent baseline under offline runtime.

### CI Dry-Run (Local Equivalent Commands)

- Command: `mvn -q -DskipTests validate`
- Command: `mvn -q -pl model -am test`
- Result: both failed at Enforcer `RequireJavaVersion`
- Gate Behavior: expected and desired; quality gate blocks non-Java-21 environments.

## Handoff Notes

- To achieve a passing runtime smoke baseline, start gateway + required services and rerun `scripts/smoke-core.ps1`.
- For CI parity locally, ensure Maven runs with Java 21 (`JAVA_HOME` and `mvn -v` runtime).
