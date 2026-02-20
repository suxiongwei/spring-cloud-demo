$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$catalogPath = Join-Path $repoRoot "config/scenario-catalog.json"
$serviceDemoPath = Join-Path $repoRoot "gateway/src/main/resources/static/js/service-demo.js"

if (!(Test-Path $catalogPath)) { throw "Missing: $catalogPath" }
if (!(Test-Path $serviceDemoPath)) { throw "Missing: $serviceDemoPath" }

$catalog = Get-Content -Raw $catalogPath | ConvertFrom-Json
if ($null -eq $catalog.ui) { throw "scenario-catalog.json missing ui section" }
if ($null -eq $catalog.ui.mainStageIds -or $null -eq $catalog.ui.roadmapStageIds) {
    throw "scenario-catalog.json missing ui.mainStageIds or ui.roadmapStageIds"
}

$coreExpected = @('sentinel','nacos','gateway','sca','dubbo','redis','seata','rocketmq')
$roadmapExpected = @('higress','opentelemetry','k8s','opensergo','chaosblade','appactive','schedulerx','arctic')

function Compare-Set {
    param([string[]]$Expected, [string[]]$Actual, [string]$Label)

    $expectedOnly = $Expected | Where-Object { $_ -notin $Actual }
    $actualOnly = $Actual | Where-Object { $_ -notin $Expected }

    if ($expectedOnly.Count -gt 0 -or $actualOnly.Count -gt 0) {
        Write-Host "$Label mismatch" -ForegroundColor Red
        if ($expectedOnly.Count -gt 0) { Write-Host "  Missing: $($expectedOnly -join ', ')" -ForegroundColor Red }
        if ($actualOnly.Count -gt 0) { Write-Host "  Unexpected: $($actualOnly -join ', ')" -ForegroundColor Red }
        exit 1
    }
}

$coreActual = @($catalog.ui.mainStageIds)
$roadmapActual = @($catalog.ui.roadmapStageIds)
Compare-Set -Expected $coreExpected -Actual $coreActual -Label "core stage ids"
Compare-Set -Expected $roadmapExpected -Actual $roadmapActual -Label "roadmap stage ids"

$capabilityMap = @{}
foreach ($cap in $catalog.capabilities) {
    $capabilityMap[$cap.id] = $cap.maturity
}

foreach ($id in $coreExpected) {
    if (!$capabilityMap.ContainsKey($id)) {
        throw "core capability missing from catalog.capabilities: $id"
    }
    if ($capabilityMap[$id] -ne 'implemented') {
        throw "core capability must be implemented: $id => $($capabilityMap[$id])"
    }
}

$serviceDemoJs = Get-Content -Raw $serviceDemoPath
if ($serviceDemoJs -notmatch "coreStageIds\s*:\s*\[") {
    throw "service-demo.js missing coreStageIds declaration"
}
if ($serviceDemoJs -notmatch "roadmapStageIds\s*:\s*\[") {
    throw "service-demo.js missing roadmapStageIds declaration"
}
if ($serviceDemoJs -notmatch "showRoadmapSection") {
    throw "service-demo.js missing showRoadmapSection flag"
}

Write-Host "Core stage validation passed." -ForegroundColor Green
Write-Host "core count: $($coreActual.Count)"
Write-Host "roadmap count: $($roadmapActual.Count)"
