## ADDED Requirements

### Requirement: Frontend endpoint declarations shall match backend contracts
All demo endpoints referenced by gateway static UI configuration MUST resolve to existing backend routes or explicitly marked compatibility aliases.

#### Scenario: Contract drift is detected
- **WHEN** a frontend-configured endpoint does not exist in the backend contract source
- **THEN** contract validation SHALL fail before merge

### Requirement: Contract source shall be authoritative
The project MUST maintain a single authoritative endpoint contract source that frontend demo configuration is generated from or validated against.

#### Scenario: Endpoint update propagates consistently
- **WHEN** a backend demo endpoint path changes in the authoritative contract source
- **THEN** frontend endpoint configuration SHALL be updated or validated in the same change
