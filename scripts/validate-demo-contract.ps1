$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$catalogPath = Join-Path $repoRoot "config/scenario-catalog.json"
$serviceDemoPath = Join-Path $repoRoot "gateway/src/main/resources/static/js/service-demo.js"
$servicesConfigPath = Join-Path $repoRoot "gateway/src/main/resources/static/js/config/services-config.js"

if (!(Test-Path $catalogPath)) {
    throw "Missing scenario catalog: $catalogPath"
}
if (!(Test-Path $serviceDemoPath)) {
    throw "Missing file: $serviceDemoPath"
}
if (!(Test-Path $servicesConfigPath)) {
    throw "Missing file: $servicesConfigPath"
}

$catalog = Get-Content -Raw $catalogPath | ConvertFrom-Json

$allowedPatterns = @()
foreach ($pattern in $catalog.contract.allowedEndpointPatterns) {
    $allowedPatterns += [regex]::new($pattern)
}

$legacyAliasMap = @{}
foreach ($alias in $catalog.contract.legacyAliases) {
    $legacyAliasMap[$alias.legacyPath] = $alias.canonicalPath
}

function Get-PathOnly([string]$endpoint) {
    if ([string]::IsNullOrWhiteSpace($endpoint)) {
        return ""
    }
    $trimmed = $endpoint.Trim()
    $idx = $trimmed.IndexOf("?")
    if ($idx -ge 0) {
        return $trimmed.Substring(0, $idx)
    }
    return $trimmed
}

function Add-Endpoint([hashtable]$set, [string]$value) {
    if ([string]::IsNullOrWhiteSpace($value)) {
        return
    }
    $normalized = Get-PathOnly $value
    if ($normalized.StartsWith("/api/")) {
        $set[$normalized] = $true
    }
}

$endpoints = @{}

$serviceDemoContent = Get-Content -Raw $serviceDemoPath
$servicesConfigContent = Get-Content -Raw $servicesConfigPath

# endpoint map literals in service-demo.js: 'key': '/api/...'
$mapMatches = [regex]::Matches($serviceDemoContent, "'[^']+'\s*:\s*'(/api/[^']*)'")
foreach ($m in $mapMatches) {
    Add-Endpoint -set $endpoints -value $m.Groups[1].Value
}

# endpoint fields in services-config.js: endpoint: '/api/...'
$cfgMatches = [regex]::Matches($servicesConfigContent, "endpoint:\s*'(/api/[^']*)'")
foreach ($m in $cfgMatches) {
    Add-Endpoint -set $endpoints -value $m.Groups[1].Value
}

$missing = @()
foreach ($ep in $endpoints.Keys | Sort-Object) {
    $isLegacy = $legacyAliasMap.ContainsKey($ep)
    $isAllowed = $false
    foreach ($pattern in $allowedPatterns) {
        if ($pattern.IsMatch($ep)) {
            $isAllowed = $true
            break
        }
    }

    if (!($isLegacy -or $isAllowed)) {
        $missing += $ep
    }
}

if ($missing.Count -gt 0) {
    Write-Host "Contract validation failed. Endpoints missing from canonical patterns or legacy aliases:" -ForegroundColor Red
    foreach ($ep in $missing) {
        Write-Host "  - $ep" -ForegroundColor Red
    }
    exit 1
}

Write-Host "Contract validation passed." -ForegroundColor Green
Write-Host "Checked endpoints: $($endpoints.Count)" -ForegroundColor Green
