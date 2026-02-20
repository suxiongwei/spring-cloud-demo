# Dubbo Scenario Kit

## Preconditions

- `service-order-dubbo` and `service-product-dubbo` are running.
- Nacos registry is reachable.

## Inputs

- Sync call: `GET /api/order/dubbo/call-sync?productId=1`
- Batch call: `GET /api/order/dubbo/call-batch`
- Timeout call: `GET /api/order/dubbo/call-timeout?productId=1&sleepTime=4000`
- Protocol compare: `GET /api/order/dubbo/protocol/compare?productId=1&requestCount=5`

## Commands

```bash
curl "http://localhost:9090/api/order/dubbo/call-sync?productId=1"
curl "http://localhost:9090/api/order/dubbo/call-batch"
curl "http://localhost:9090/api/order/dubbo/call-timeout?productId=1&sleepTime=4000"
curl "http://localhost:9090/api/order/dubbo/protocol/compare?productId=1&requestCount=5"
```

## Expected Outputs

- Sync and batch calls return successful RPC data payload.
- Timeout call returns timeout/failure detail when threshold is exceeded.
- Protocol compare returns per-protocol timing summary.
