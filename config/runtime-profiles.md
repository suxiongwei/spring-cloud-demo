# Runtime Profiles Guide

## Default Local Profile

The following services now default to `local` profile:

- `service-order`
- `service-order-dubbo`
- `service-product-dubbo`

## Environment Variables

Copy `.env.example` and export values in your shell before launching services.

Core variables:

- `NACOS_SERVER_ADDR`
- `NACOS_NAMESPACE`
- `NACOS_USERNAME`
- `NACOS_PASSWORD`
- `SENTINEL_DASHBOARD`
- `GATEWAY_SERVER_PORT`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`

## Notes

- For local execution, defaults target `127.0.0.1` services.
- `gateway` default port is `9090` to avoid privileged port conflicts.
