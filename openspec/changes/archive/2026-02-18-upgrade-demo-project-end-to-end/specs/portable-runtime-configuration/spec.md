## ADDED Requirements

### Requirement: Runtime configuration shall be environment-parameterized
Service runtime configuration for registry addresses, namespaces, credentials, and key ports MUST be parameterized through environment variables with local-safe defaults.

#### Scenario: Project runs in a new machine without source edits
- **WHEN** a developer clones the repository in a clean environment
- **THEN** the project SHALL start using documented environment variables and default local profile values without editing committed config files

### Requirement: Local profile shall be first-class
The system MUST provide a dedicated local profile for demo execution that avoids privileged ports and machine-specific hardcoding.

#### Scenario: Local profile is activated
- **WHEN** the local profile is selected
- **THEN** services SHALL bind to non-privileged default ports and consume local-safe dependency endpoints
