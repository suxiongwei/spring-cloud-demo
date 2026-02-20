param(
    [string]$BaseUrl = "http://localhost:9090",
    [string]$MarkdownOutput = "reports/interview-kit-report.md",
    [string]$JsonOutput = "reports/interview-kit-report.json",
    [int]$TimeoutSec = 12
)

$ErrorActionPreference = "Continue"

function Invoke-InterviewStep {
    param(
        [string]$Name,
        [string]$Path
    )

    $url = "$BaseUrl$Path"
    $start = Get-Date
    try {
        $resp = Invoke-RestMethod -Method Get -Uri $url -TimeoutSec $TimeoutSec
        $elapsed = [int]((Get-Date) - $start).TotalMilliseconds

        $code = if ($resp.PSObject.Properties.Name -contains "code") { [int]$resp.code } else { 200 }
        $ok = ($code -eq 200)

        $scenarioId = $null
        if ($resp.PSObject.Properties.Name -contains "data" -and $null -ne $resp.data) {
            if ($resp.data.PSObject.Properties.Name -contains "scenarioId") {
                $scenarioId = [string]$resp.data.scenarioId
            }
        }

        return [PSCustomObject]@{
            name = $Name
            path = $Path
            url = $url
            success = $ok
            code = $code
            elapsedMs = $elapsed
            scenarioId = $scenarioId
            message = if ($ok) { "ok" } else { "unexpected code $code" }
        }
    } catch {
        $elapsed = [int]((Get-Date) - $start).TotalMilliseconds
        return [PSCustomObject]@{
            name = $Name
            path = $Path
            url = $url
            success = $false
            code = 0
            elapsedMs = $elapsed
            scenarioId = $null
            message = $_.Exception.Message
        }
    }
}

$cases = @(
    @{ name = "chain-a-gateway"; path = "/api/order/demo/gateway-routing" },
    @{ name = "chain-b-dubbo"; path = "/api/order/dubbo/call-sync?productId=1" },
    @{ name = "chain-c-seata"; path = "/api/business/purchase/tcc/verify?userId=U1001&commodityCode=P0001&count=1&fail=false" },
    @{ name = "chain-d-rocketmq"; path = "/api/order/demo/rocketmq/publish-basic" }
)

$results = @()
foreach ($case in $cases) {
    $results += Invoke-InterviewStep -Name $case.name -Path $case.path
}

$total = $results.Count
$passed = @($results | Where-Object { $_.success }).Count
$failed = $total - $passed

$report = [PSCustomObject]@{
    generatedAt = (Get-Date).ToString("o")
    baseUrl = $BaseUrl
    total = $total
    passed = $passed
    failed = $failed
    results = $results
}

$mdLines = @()
$mdLines += "# Interview Kit Report"
$mdLines += ""
$mdLines += "- GeneratedAt: $($report.generatedAt)"
$mdLines += "- BaseUrl: $BaseUrl"
$mdLines += "- Summary: Passed $passed / $total"
$mdLines += ""
$mdLines += "| Step | Path | ScenarioId | Success | Code | ElapsedMs | Message |"
$mdLines += "|---|---|---|---|---:|---:|---|"
foreach ($r in $results) {
    $mdLines += "| $($r.name) | `$($r.path)` | $($r.scenarioId) | $($r.success) | $($r.code) | $($r.elapsedMs) | $($r.message.Replace('|','/')) |"
}

$mdPath = Join-Path (Get-Location) $MarkdownOutput
$jsonPath = Join-Path (Get-Location) $JsonOutput

foreach ($path in @($mdPath, $jsonPath)) {
    $dir = Split-Path -Parent $path
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir | Out-Null
    }
}

$mdLines -join [Environment]::NewLine | Set-Content -Path $mdPath -Encoding UTF8
$report | ConvertTo-Json -Depth 6 | Set-Content -Path $jsonPath -Encoding UTF8

Write-Host "Interview report written: $mdPath"
Write-Host "Interview JSON written: $jsonPath"
Write-Host "Passed: $passed / $total"

if ($failed -gt 0) {
    exit 1
}
