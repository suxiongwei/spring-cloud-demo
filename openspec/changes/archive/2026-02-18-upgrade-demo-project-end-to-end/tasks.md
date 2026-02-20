## 1. Baseline and Contract Foundation

- [x] 1.1 Create `scenario-catalog` source file with capability id, maturity, endpoint, dependencies, and verification fields
- [x] 1.2 Define and enforce maturity enum values (`implemented`, `demo-only`, `planned`)
- [x] 1.3 Inventory current demo endpoints from gateway static config and backend controllers
- [x] 1.4 Add contract alignment validation that fails when frontend endpoints are missing in backend contract source
- [x] 1.5 Add compatibility alias mapping rules for legacy endpoints that must be temporarily preserved
- [x] 1.6 Update gateway static demo endpoint references to align with validated contract source

## 2. Runtime Portability and Toolchain Consistency

- [x] 2.1 Parameterize Nacos/Sentinel/Redis/Seata addresses, namespace, credentials, and key ports via environment variables
- [x] 2.2 Add local-safe defaults and introduce dedicated `local` profile for gateway and core services
- [x] 2.3 Replace privileged/default-conflicting runtime ports with local-friendly defaults where needed
- [x] 2.4 Add `.env.example` and profile-oriented runtime variable documentation artifacts
- [x] 2.5 Add Maven Enforcer rules for required Java and Maven versions
- [x] 2.6 Ensure local and CI build commands execute identical toolchain validation checks

## 3. Scenario Verification Kits and Smoke Entry

- [x] 3.1 Define standardized Sentinel scenario kit inputs, commands, and expected outputs
- [x] 3.2 Define standardized Dubbo scenario kit inputs, commands, and expected outputs
- [x] 3.3 Define standardized Redis scenario kit inputs, commands, and expected outputs
- [x] 3.4 Implement Seata commit verification outputs with pre/post resource state evidence
- [x] 3.5 Implement Seata rollback verification with deterministic failure injection path
- [x] 3.6 Create one-command smoke script covering discovery, invocation, resilience, and transaction core paths
- [x] 3.7 Add machine-readable smoke report summary (pass/fail per scenario)

## 4. Modularization and Shared Model Unification

- [x] 4.1 Split oversized `OrderController` responsibilities into domain-oriented service/facade components
- [x] 4.2 Split oversized `RedisTestController` into thematic controllers while preserving endpoint compatibility
- [x] 4.3 Split oversized `ProductDubboClient` into focused client/service collaborators
- [x] 4.4 Move shared response envelope and error code definitions into common model module
- [x] 4.5 Refactor service modules to use centralized response contracts and remove duplicated local definitions
- [x] 4.6 Run compatibility checks to confirm endpoint behavior remains functionally equivalent after refactor

## 5. Test Architecture and CI Quality Gates

- [x] 5.1 Add unit tests for key fallback/rule-processing logic in Sentinel, Dubbo, and Redis flows
- [x] 5.2 Add integration tests for gateway routing and front-to-back contract-critical endpoints
- [x] 5.3 Add integration tests for Seata commit/rollback verification scenarios
- [x] 5.4 Add integration tests for cross-service Feign/Dubbo invocation critical paths
- [x] 5.5 Configure CI baseline pipeline with build, test, and smoke stages
- [x] 5.6 Define explicit quality gate criteria in version-controlled CI configuration
- [x] 5.7 Ensure local commands can reproduce CI quality gate outcomes

## 6. Guided Interview Flow and Observability Closure

- [x] 6.1 Implement guided interview demo flow orchestration for ordered scenario execution
- [x] 6.2 Add structured evidence output per guided step (request, response summary, key metric/rule result)
- [x] 6.3 Implement minimal end-to-end OpenTelemetry trace propagation across gateway and downstream services
- [x] 6.4 Add local-toggle configuration for enabling/disabling tracing without breaking core demos
- [x] 6.5 Mark advanced topics (RocketMQ/Chaos/Higress/OpenSergo/SchedulerX/AppActive) with explicit implementation status in catalog and UI metadata
- [x] 6.6 Add roadmap metadata for `planned` advanced topics (milestone, minimal deliverable, prerequisites)

## 7. Final Validation and Release Readiness

- [x] 7.1 Execute full smoke flow in local profile and capture result baseline
- [x] 7.2 Execute CI pipeline dry run and verify quality gate behavior
- [x] 7.3 Validate scenario catalog completeness against all visible demo entries
- [x] 7.4 Validate no unresolved frontend-backend contract drift remains
- [x] 7.5 Prepare final change summary and acceptance evidence for archive/implementation handoff
