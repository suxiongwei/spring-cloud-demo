# Redis Scenario Kit

## Preconditions

- Redis server is running and reachable by `service-order-dubbo`.
- `service-order-dubbo` is running.

## Inputs

- Data structure: `GET /api/order/dubbo/redis/string`
- Distributed lock: `GET /api/order/dubbo/redis/lock/basic`
- Cache breakdown: `GET /api/order/dubbo/redis/cache/breakdown`
- Rate limit token bucket: `GET /api/order/dubbo/redis/rate-limit/token`

## Commands

```bash
curl "http://localhost:9090/api/order/dubbo/redis/string"
curl "http://localhost:9090/api/order/dubbo/redis/lock/basic"
curl "http://localhost:9090/api/order/dubbo/redis/cache/breakdown"
curl "http://localhost:9090/api/order/dubbo/redis/rate-limit/token"
```

## Expected Outputs

- Each endpoint returns structured test result and operation summary.
- Lock/rate-limit endpoints show deterministic success/fail conditions.
- Cache endpoints return scenario-specific mitigation evidence.
