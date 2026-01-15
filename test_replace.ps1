$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$pattern = "(?s)<div class=`"result-display`"[^>]*:class=`"getResultDisplay\('sentinel-qps'\)\?\.status`"[^>]*>.*?<div class=`"result-link-hint`">\s*点击查看详情\s*</div>\s*</div>"

$replacement = "<result-display :result=`"getResultDisplay('sentinel-qps')`" title=`"测试结果`" test-id=`"sentinel-qps`" @show-detail=`"showResultDetail`"></result-display>"

if ($content -match $pattern) {
    $content = $content -replace $pattern, $replacement
    Write-Host "Replaced sentinel-qps"
} else {
    Write-Host "Pattern not found for sentinel-qps"
}

$content | Out-File $htmlPath -Encoding UTF8
Write-Host "Test replacement completed"
