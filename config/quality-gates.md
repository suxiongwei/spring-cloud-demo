# Quality Gates

## CI Pipeline

Workflow: `.github/workflows/ci-baseline.yml`

Stages:

1. `build`
- Command: `mvn -q -DskipTests validate`
- Gate: must pass Maven Enforcer (Java 21, Maven 3.9+) and validation lifecycle.

2. `test`
- Command: `mvn -q -pl model -am test`
- Gate: baseline automated test stage must pass.

3. `smoke`
- Commands:
  - `scripts/validate-demo-contract.ps1`
  - `scripts/validate-scenario-catalog.ps1`
- Gate: contract and catalog smoke checks must pass.

## Local Repro Commands

Use the same command set locally:

```powershell
mvn -q -DskipTests validate
mvn -q -pl model -am test
powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-scenario-catalog.ps1
```

Or use wrappers where applicable:

- `scripts/build-validate.ps1`
- `scripts/build-validate.sh`
