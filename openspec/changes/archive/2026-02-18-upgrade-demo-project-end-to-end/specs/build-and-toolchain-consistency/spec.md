## ADDED Requirements

### Requirement: Build shall fail fast on incompatible toolchain
The Maven build MUST validate Java and Maven version requirements before compilation and SHALL terminate with actionable diagnostics when requirements are not met.

#### Scenario: Incompatible Java runtime is used
- **WHEN** a build is executed with an unsupported Java runtime
- **THEN** the build SHALL fail in the validation phase with a message that states required and detected versions

### Requirement: Toolchain requirements shall be explicit in CI and local builds
The same toolchain constraints MUST apply to both local developer builds and CI pipelines.

#### Scenario: CI and local produce consistent validation outcome
- **WHEN** the same commit is built locally and in CI under matching toolchain versions
- **THEN** both environments SHALL pass or fail toolchain validation consistently
