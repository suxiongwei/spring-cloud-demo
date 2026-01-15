$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$additionalReplacements = @{
    'style="width:200px">' = 'class="width-200">'
    'style="width:200px"' = 'class="width-200"'
    'style="flex:1"' = 'class="flex-1"'
}

foreach ($key in $additionalReplacements.Keys) {
    $value = $additionalReplacements[$key]
    $content = $content -replace [regex]::Escape($key), $value
}

$content | Out-File $htmlPath -Encoding UTF8
Write-Host "Remaining inline styles replaced successfully"
