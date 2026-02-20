# sentinel-dubbo-redis-benchmark-kits Specification

## Purpose
TBD - created by archiving change upgrade-demo-project-end-to-end. Update Purpose after archive.
## Requirements
### Requirement: Governance and RPC/cache scenarios shall have standardized benchmark kits
Sentinel, Dubbo, and Redis demo scenarios MUST provide standardized benchmark inputs, execution commands, and expected outcome definitions.

#### Scenario: Scenario kit is reusable
- **WHEN** different developers run the same benchmark kit
- **THEN** they SHALL be able to reproduce comparable scenario outcomes using the documented inputs and commands

### Requirement: Benchmark kits shall be interview-oriented and evidence-backed
Each benchmark kit MUST include concise interpretation guidance and concrete result evidence fields (for example throughput, latency, block/fallback counts, or distribution).

#### Scenario: Benchmark output supports explanation
- **WHEN** a benchmark run completes
- **THEN** output SHALL include both raw results and a mapped explanation field suitable for interview discussion

