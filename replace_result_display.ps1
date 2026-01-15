$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$testIds = @(
    'sentinel-qps', 'sentinel-thread', 'sentinel-hot',
    'nacos-services', 'nacos-config',
    'dubbo-batch', 'dubbo-list-all', 'dubbo-timeout', 'dubbo-exception', 'dubbo-async', 'dubbo-region',
    'dubbo-concurrency', 'dubbo-leastactive',
    'dubbo-filter', 'dubbo-version-group',
    'protocol-compare', 'protocol-dubbo', 'protocol-triple', 'protocol-rest',
    'seata-tcc-ok', 'seata-tcc-fail',
    'gateway-routing', 'gateway-auth-pass', 'gateway-auth-fail',
    'higress-routing', 'higress-auth-pass', 'higress-auth-fail',
    'feign-enhanced', 'load-balance'
)

$replacements = 0

foreach ($testId in $testIds) {
    $pattern = "(?s)<div class=`"result-display`"[^>]*:class=`"getResultDisplay\('$testId'\)\?\.status`"[^>]*>.*?<div class=`"result-link-hint`">\s*点击查看详情\s*</div>\s*</div>"
    
    $replacement = "<result-display :result=`"getResultDisplay('$testId')`" title=`"测试结果`" test-id=`"$testId`" @show-detail=`"showResultDetail`"></result-display>"
    
    if ($content -match $pattern) {
        $content = $content -replace $pattern, $replacement
        $replacements++
        Write-Host "Replaced: $testId"
    }
}

Write-Host "Total replacements: $replacements"

$content | Out-File $htmlPath -Encoding UTF8
Write-Host "Result display components replaced successfully"
