# multi-layer-test-system Specification

## Purpose
TBD - created by archiving change upgrade-demo-project-end-to-end. Update Purpose after archive.
## Requirements
### Requirement: Test strategy shall include unit, integration, and smoke layers
The project MUST define and maintain a layered test strategy covering unit tests, integration tests, and smoke tests for core demo flows.

#### Scenario: Layered tests exist for critical flows
- **WHEN** core flows are identified (gateway routing, feign/dubbo call, sentinel protection, seata transaction)
- **THEN** each flow SHALL be covered by at least one applicable test layer

### Requirement: Smoke tests shall be executable as a single entrypoint
The system MUST provide a single command or script entrypoint to run smoke verification for critical demo paths.

#### Scenario: One-command smoke verification
- **WHEN** a maintainer runs the smoke test entrypoint
- **THEN** the script SHALL execute predefined critical API checks and report pass/fail summary

