#!/usr/bin/env bash
set -euo pipefail

# Keep local and CI validation command identical.
mvn -q -DskipTests validate
mvn -q -pl services/service-order -am "-Dtest=OrderControllerRocketMqTest,ScenarioControlControllerTest,CoreScenarioEvidenceContractTest,OrderDemoFacadeGuidedFlowTest,RocketMqDemoFacadeTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -q -pl services/service-order-dubbo -am "-Dtest=OrderDubboControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -q -pl services/seata-business -am "-Dtest=PurchaseRestControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test

if ! command -v pwsh >/dev/null 2>&1; then
  echo "pwsh is required to run validation scripts (*.ps1)" >&2
  exit 1
fi

pwsh -NoLogo -NoProfile -File scripts/validate-demo-contract.ps1
pwsh -NoLogo -NoProfile -File scripts/validate-scenario-catalog.ps1
pwsh -NoLogo -NoProfile -File scripts/validate-core-stage.ps1
