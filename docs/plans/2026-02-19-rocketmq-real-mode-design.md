# Real RocketMQ Mode Design

Date: 2026-02-19

## Goal
Switch existing 10 RocketMQ business scenarios from in-memory simulation to strict real RocketMQ runtime mode using actual NameServer + Broker.

## Decisions
- Runtime stack: `rocketmq-spring-boot-starter`
- Mode policy: strict real mode; if MQ unavailable or scenario not completed in time, API returns failure.
- Scope: keep existing 10 API paths unchanged (`/api/order/demo/rocketmq/*`).

## Architecture
- `RocketMqDemoFacade` orchestrates scenario flow and waits for consumer-side evidence.
- New runtime state component tracks per-message processing evidence.
- Producer uses `RocketMQTemplate` with real send APIs (`syncSend`, `syncSendOrderly`, delayed send, transactional send).
- Consumers are concrete `@RocketMQMessageListener` implementations (basic/retry/dlq/idempotent/orderly/delay/tag/tx observer).
- Transaction listener handles scenario 07/08 with commit + unknown/check paths.

## Error Handling
- Send failure, broker connect failure, timeout waiting for expected consumption evidence => `ApiResponse.fail(INTERNAL_ERROR, ...)`.
- No mock fallback.

## Infra
- Add `docker-compose.rocketmq.yml` with `namesrv` + `broker`.
- Add `deploy/rocketmq/broker.conf` with low transaction check interval for demo speed.

## Verification
- Unit/API tests in `service-order`.
- Contract script validation.
- Manual smoke via compose + API calls.
