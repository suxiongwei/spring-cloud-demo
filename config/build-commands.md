# Build Validation Commands

Use the same validation command set in local and CI:

```bash
mvn -q -DskipTests validate
mvn -q -pl model -am test
powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-scenario-catalog.ps1
powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1
```

Wrapper scripts:

- Windows PowerShell: `scripts/build-validate.ps1`
- Bash: `scripts/build-validate.sh`
- Interview dry-run: `scripts/run-interview-kit.ps1`

Both wrappers execute the same Maven command so Enforcer checks run consistently.
