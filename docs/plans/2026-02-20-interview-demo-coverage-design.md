# Interview-Focused Demo Coverage Design (Java Backend First)

**Date:** 2026-02-20
**Owner:** xiongweisu (learning project)

## 1. Context

Current repository already has a broad demo surface (service governance, RPC, distributed transaction, MQ, cache), but maturity is uneven:

- `config/scenario-catalog.json` shows 16 capabilities, with 8 implemented, 2 demo-only, 6 planned.
- Demo contract guardrails already exist: `scripts/validate-demo-contract.ps1`, `scripts/validate-scenario-catalog.ps1`.
- There is an initial interview flow endpoint: `/api/order/demo/guided-flow`.

For interview outcomes, risk is not lack of feature count, but weak "evidence chain":

- hard to prove scenario success/failure deterministically
- too many entry points for a 30-minute narrative
- not enough standardized outputs for follow-up questions

## 2. Target (1-2 months)

Build an interview-ready demo stage that can finish 4 business chains in 30 minutes, with reproducible evidence, while staying aligned with Java backend roles and middleware-heavy roles.

Success criteria:

1. Core stage keeps only mature capabilities (`implemented`).
2. Every core scenario returns structured evidence (not only text descriptions).
3. One command can execute and export interview report.
4. CI validates core stage and contract alignment.

## 3. Options and Trade-offs

### Option A: Breadth First

- Convert planned/demo-only capabilities to runnable quickly.
- Pros: more badges and topics.
- Cons: shallow quality; weak defense under deep interview probing.

### Option B: Interview Script First (Recommended)

- Curate a compact core stage around 4 high-value chains.
- Pros: best interview ROI; deeper technical storytelling.
- Cons: visible capability count grows slower.

### Option C: Engineering Quality First

- Invest first in tests/observability/reliability framework.
- Pros: stronger engineering baseline.
- Cons: less immediate showcase impact.

Recommendation: use **Option B first**, then apply Option C enhancements.

## 4. Core Design

### 4.1 Capability Partition

Core stage (visible by default):

- `sentinel`
- `nacos`
- `gateway`
- `sca`
- `dubbo`
- `redis`
- `seata`
- `rocketmq`

Roadmap stage (secondary page/panel):

- `higress`, `opentelemetry` (demo-only)
- `k8s`, `opensergo`, `chaosblade`, `appactive`, `schedulerx`, `arctic` (planned)

### 4.2 Four Interview Chains

1. Chain A: Request entry and basic governance
`Gateway -> Nacos discovery -> Feign call -> Sentinel protection`

2. Chain B: High-performance RPC
`Order Dubbo -> Product Dubbo`, covering timeout, async, load-balance, version/group, protocol compare

3. Chain C: Strong consistency transaction
`Seata TCC verify`, including commit path and rollback path with snapshots

4. Chain D: Eventual consistency
`RocketMQ publish/retry/DLQ/idempotent/tx-check/replay`

### 4.3 Evidence Contract

Unify scenario response payload with minimal common fields:

- `scenarioId`
- `success`
- `failureInjected`
- `costMs`
- `evidence` (map/list, scenario-specific proof)
- `nextQuestionHints` (interview drill-down prompts)

### 4.4 Guided Flow v2

Upgrade `/api/order/demo/guided-flow` from static step descriptions to executable script metadata:

- includes step endpoint + assertion rule + expected branch (success/fail)
- supports generated run-id
- supports report assembly by script tool

## 5. Error Handling and Repeatability

1. Add deterministic reset operations for Seata, Redis, RocketMQ demo state.
2. Add explicit failure injection switches for key paths (TCC fail, MQ consume fail).
3. Keep failures visible and explainable in response payload instead of generic error text.

## 6. Verification Strategy

1. Unit tests for response normalization and scenario-specific evidence extraction.
2. Controller tests for core chain endpoints.
3. Script-level smoke run to generate a single interview report.
4. Keep contract checks as CI gates.

## 7. 8-Week Delivery Rhythm

- Week 1-2: Stage simplification and chain curation.
- Week 3-4: Evidence contract unification and guided-flow v2.
- Week 5-6: Reset/failure-injection + report exporter.
- Week 7: Mock interview drills (3 rounds, 30 min each).
- Week 8: FAQ and comparison playbook for follow-up questions.

## 8. Non-goals (for this cycle)

- Full production-grade cloud-native platformization (K8s operators, full OTel backend, etc.).
- Implementing all planned capabilities before interview season.
- Pursuing visual complexity over deterministic technical evidence.

## 9. Risks

1. Over-implementation of roadmap items may dilute focus.
2. Scripted success without failure evidence may hurt credibility.
3. Inconsistent response schemas across modules may slow integration.

Mitigation: lock core stage boundaries and enforce shared evidence fields in tests.
