# Sentinel Scenario Kit

## Preconditions

- `service-order` is running and registered.
- Sentinel rules are configured in Nacos or dashboard.

## Inputs

- QPS test endpoint: `GET /api/order/rateLimit/qps`
- Thread test endpoint: `GET /api/order/rateLimit/thread`
- Hot parameter test endpoint: `GET /api/order/hotspot/param?userId=1001&productId=2002`
- Degrade test endpoint: `GET /api/order/degrade/rt`

## Commands

```bash
curl "http://localhost:9090/api/order/rateLimit/qps"
curl "http://localhost:9090/api/order/rateLimit/thread"
curl "http://localhost:9090/api/order/hotspot/param?userId=1001&productId=2002"
curl "http://localhost:9090/api/order/degrade/rt"
```

## Expected Outputs

- QPS/thread/hotspot endpoints return success in low load.
- Under high load, response contains service-defined block/fail message.
- Degrade endpoint returns fallback or block result when degrade rule is active.
