## ADDED Requirements

### Requirement: System shall maintain a single scenario catalog
The project MUST define a single machine-readable scenario catalog that lists each demo scenario, capability id, maturity level, backend endpoint mapping, dependencies, and verification entry.

#### Scenario: Catalog contains complete scenario metadata
- **WHEN** a maintainer adds or updates a demo scenario
- **THEN** the catalog SHALL include capability id, maturity level, endpoint, dependency list, and verification reference for that scenario

### Requirement: System shall enforce maturity taxonomy
Each scenario in the catalog MUST use one of the allowed maturity values: `implemented`, `demo-only`, or `planned`.

#### Scenario: Invalid maturity value is rejected
- **WHEN** a scenario uses a maturity value outside the allowed set
- **THEN** catalog validation SHALL fail with a clear error
