# advanced-topic-implementation-statusing Specification

## Purpose
TBD - created by archiving change upgrade-demo-project-end-to-end. Update Purpose after archive.
## Requirements
### Requirement: Advanced topics shall have explicit implementation status
Each advanced topic (for example RocketMQ, Chaos, Higress, OpenSergo, SchedulerX, AppActive) MUST declare an explicit implementation status and scope in the scenario catalog and UI metadata.

#### Scenario: Topic status is visible to users
- **WHEN** a user views an advanced topic in the demo UI
- **THEN** the UI SHALL display whether the topic is implemented, demo-only, or planned

### Requirement: Planned topics shall include actionable roadmap metadata
Topics marked as planned MUST include roadmap metadata defining target milestone, minimal deliverable, and dependency prerequisites.

#### Scenario: Planned topic has actionable next steps
- **WHEN** a maintainer inspects a planned advanced topic
- **THEN** roadmap metadata SHALL provide concrete next implementation steps and required prerequisites

