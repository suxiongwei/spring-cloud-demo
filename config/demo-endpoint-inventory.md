# Demo Endpoint Inventory

Last Updated: 2026-02-17

## Scope

- Frontend endpoint declarations:
  - `gateway/src/main/resources/static/js/service-demo.js`
  - `gateway/src/main/resources/static/js/config/services-config.js`
- Backend endpoint declarations:
  - `services/service-order/src/main/java/indi/mofan/order/controller/OrderController.java`
  - `services/seata-business/src/main/java/indi/mofan/business/controller/PurchaseRestController.java`
  - `services/service-order-dubbo/src/main/java/indi/mofan/order/controller/OrderDubboController.java`
  - `services/service-order-dubbo/src/main/java/indi/mofan/order/controller/RedisTestController.java`

## Canonical Frontend Contract (Post-Alignment)

- Sentinel:
  - `/api/order/rateLimit/qps`
  - `/api/order/rateLimit/thread`
  - `/api/order/hotspot/param`
  - `/api/order/degrade/rt`
- Nacos:
  - `/api/order/demo/nacos/services`
  - `/api/order/demo/nacos-config`
- SCA:
  - `/api/order/demo/feign/call-enhanced`
  - `/api/order/demo/load-balance`
- Seata:
  - `/api/business/purchase/tcc/verify`
- Dubbo + Redis:
  - `/api/order/dubbo/**`
- Other demo:
  - `/api/order/demo/gateway-routing`
  - `/api/order/demo/tracing`
  - `/api/order/demo/rocketmq/**`
  - `/api/order/config`

## Temporary Legacy Aliases

- `/api/order/demo/flow-control` -> `/api/order/rateLimit/qps`
- `/api/order/demo/hot-param` -> `/api/order/hotspot/param`
- `/api/order/demo/degrade` -> `/api/order/degrade/rt`
- `/api/order/demo/circuit-breaker` -> `/api/order/degrade/rt`
- `/api/order/demo/nacos/nacos-config` -> `/api/order/demo/nacos-config`
- `/api/order/seata/tcc/commit` -> `/api/business/purchase/tcc?userId=U1001&commodityCode=P0001&count=1&fail=false`
- `/api/order/seata/tcc/rollback` -> `/api/business/purchase/tcc?userId=U1001&commodityCode=P0001&count=1&fail=true`

## Validation Command

```powershell
powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1
```
