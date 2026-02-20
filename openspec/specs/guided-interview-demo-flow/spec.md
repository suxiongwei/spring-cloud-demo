# guided-interview-demo-flow Specification

## Purpose
TBD - created by archiving change upgrade-demo-project-end-to-end. Update Purpose after archive.
## Requirements
### Requirement: System shall provide a guided interview demo flow
The demo system MUST provide a guided flow that executes core scenarios in a predefined sequence spanning service discovery, invocation, resilience controls, and distributed transaction validation.

#### Scenario: Guided flow runs end-to-end
- **WHEN** a user starts interview demo mode
- **THEN** the system SHALL guide execution through ordered steps and show the expected checkpoints for each step

### Requirement: Guided flow shall present auditable evidence for each step
Every guided step MUST output verifiable evidence fields (request, response summary, and key metric or rule outcome) suitable for interview walkthroughs.

#### Scenario: Step output is evidence-ready
- **WHEN** a guided step completes
- **THEN** output SHALL include enough structured evidence to explain correctness and behavior without requiring hidden context

