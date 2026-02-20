param(
    [string]$BaseUrl = "http://localhost:9090",
    [string]$Output = "reports/smoke-core-report.json"
)

$ErrorActionPreference = "Continue"

function Invoke-Smoke {
    param(
        [string]$Name,
        [string]$Path
    )

    $url = "$BaseUrl$Path"
    $start = Get-Date
    try {
        $resp = Invoke-RestMethod -Method Get -Uri $url -TimeoutSec 10
        $elapsed = [int]((Get-Date) - $start).TotalMilliseconds
        $ok = $false
        if ($null -ne $resp) {
            if ($resp.PSObject.Properties.Name -contains "code") {
                $ok = ($resp.code -eq 200)
            } else {
                $ok = $true
            }
        }
        return [PSCustomObject]@{
            name = $Name
            path = $Path
            url = $url
            success = $ok
            elapsedMs = $elapsed
            message = if ($ok) { "ok" } else { "unexpected response" }
        }
    } catch {
        $elapsed = [int]((Get-Date) - $start).TotalMilliseconds
        return [PSCustomObject]@{
            name = $Name
            path = $Path
            url = $url
            success = $false
            elapsedMs = $elapsed
            message = $_.Exception.Message
        }
    }
}

$cases = @(
    @{ name = "nacos-services"; path = "/api/order/demo/nacos/services" },
    @{ name = "sentinel-qps"; path = "/api/order/rateLimit/qps" },
    @{ name = "sentinel-thread"; path = "/api/order/rateLimit/thread" },
    @{ name = "dubbo-sync"; path = "/api/order/dubbo/call-sync?productId=1" },
    @{ name = "feign-enhanced"; path = "/api/order/demo/feign/call-enhanced?productId=1" },
    @{ name = "gateway-routing"; path = "/api/order/demo/gateway-routing" },
    @{ name = "seata-tcc-commit"; path = "/api/business/purchase/tcc?userId=U1001&commodityCode=P0001&count=1&fail=false" }
)

$results = @()
foreach ($case in $cases) {
    $results += Invoke-Smoke -Name $case.name -Path $case.path
}

$total = $results.Count
$passed = ($results | Where-Object { $_.success }).Count
$failed = $total - $passed

$report = [PSCustomObject]@{
    generatedAt = (Get-Date).ToString("o")
    baseUrl = $BaseUrl
    total = $total
    passed = $passed
    failed = $failed
    results = $results
}

$outPath = Join-Path (Get-Location) $Output
$outDir = Split-Path -Parent $outPath
if (!(Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}
$report | ConvertTo-Json -Depth 5 | Set-Content -Path $outPath -Encoding UTF8

Write-Host "Smoke report written to $outPath"
Write-Host "Passed: $passed / $total"

if ($failed -gt 0) {
    exit 1
}
