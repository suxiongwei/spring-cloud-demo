$ErrorActionPreference = "Stop"

function Invoke-Checked {
    param([string]$Command)

    Write-Host "> $Command"
    Invoke-Expression $Command
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed with exit code ${LASTEXITCODE}: $Command"
    }
}

# Keep local and CI validation command identical.
Invoke-Checked 'mvn -q -DskipTests validate'
Invoke-Checked 'mvn -q -pl services/service-order -am "-Dtest=OrderControllerRocketMqTest,ScenarioControlControllerTest,CoreScenarioEvidenceContractTest,OrderDemoFacadeGuidedFlowTest,RocketMqDemoFacadeTest" "-Dsurefire.failIfNoSpecifiedTests=false" test'
Invoke-Checked 'mvn -q -pl services/service-order-dubbo -am "-Dtest=OrderDubboControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test'
Invoke-Checked 'mvn -q -pl services/seata-business -am "-Dtest=PurchaseRestControllerTest" "-Dsurefire.failIfNoSpecifiedTests=false" test'
Invoke-Checked 'powershell -ExecutionPolicy Bypass -File scripts/validate-demo-contract.ps1'
Invoke-Checked 'powershell -ExecutionPolicy Bypass -File scripts/validate-scenario-catalog.ps1'
Invoke-Checked 'powershell -ExecutionPolicy Bypass -File scripts/validate-core-stage.ps1'
