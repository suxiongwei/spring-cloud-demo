$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$catalogPath = Join-Path $repoRoot "config/scenario-catalog.json"
$serviceDemoJs = Join-Path $repoRoot "gateway/src/main/resources/static/js/service-demo.js"
$servicesConfigJs = Join-Path $repoRoot "gateway/src/main/resources/static/js/config/services-config.js"

if (!(Test-Path $catalogPath)) { throw "Missing: $catalogPath" }
if (!(Test-Path $serviceDemoJs)) { throw "Missing: $serviceDemoJs" }
if (!(Test-Path $servicesConfigJs)) { throw "Missing: $servicesConfigJs" }

$catalog = Get-Content -Raw $catalogPath | ConvertFrom-Json
$catalogIds = @{}
foreach ($cap in $catalog.capabilities) {
    $catalogIds[$cap.id] = $true
}

$js = Get-Content -Raw $serviceDemoJs

# Use updateActivePanoramaTab componentMap as authoritative "visible demo entries".
$mapBlockMatch = [regex]::Match($js, "(?s)const componentMap\s*=\s*\{(.*?)\}")
if (!$mapBlockMatch.Success) {
    throw "Unable to parse componentMap from service-demo.js"
}
$mapBody = $mapBlockMatch.Groups[1].Value
$componentIds = [regex]::Matches($mapBody, "'([a-zA-Z0-9-]+)'\s*:") |
    ForEach-Object { $_.Groups[1].Value } |
    Select-Object -Unique

$visibleIds = @{}
foreach ($id in $componentIds) {
    if (![string]::IsNullOrWhiteSpace($id)) {
        $visibleIds[$id] = $true
    }
}

$missingInCatalog = @()
foreach ($id in $visibleIds.Keys) {
    if (!$catalogIds.ContainsKey($id)) {
        $missingInCatalog += $id
    }
}

if ($missingInCatalog.Count -gt 0) {
    Write-Host "Scenario catalog completeness check failed." -ForegroundColor Red
    $missingInCatalog | Sort-Object | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
    exit 1
}

Write-Host "Scenario catalog completeness check passed." -ForegroundColor Green
Write-Host "Catalog capability count: $($catalog.capabilities.Count)"
Write-Host "Visible id count: $($visibleIds.Count)"
