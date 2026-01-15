$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$replacements = @{
    'style="text-align: center; padding: 40px; color: var(--muted-foreground);"' = 'class="empty-state"'
    'style="font-size: 36px; margin-bottom: 12px;"' = 'class="empty-state-icon"'
    'style="display:flex; gap:8px;"' = 'class="flex-row"'
    'style="cursor: pointer;"' = 'class="cursor-pointer"'
    'style="text-align: center; margin-top: 8px; font-size: 10px; color: var(--text-secondary); opacity: 0.7;"' = 'class="result-link-hint"'
    'style="width: 200px"' = 'class="width-200"'
    'style="width: auto"' = 'class="width-auto"'
    'style="width:120px"' = 'class="width-120"'
    'style="width:250px"' = 'class="width-250"'
    'style="width:100px"' = 'class="width-100"'
    'style="width:60px"' = 'class="width-60"'
    'style="font-weight: 700;"' = 'class="font-bold"'
    'style="align-items: center; margin-top: 20px;"' = 'class="align-center margin-top-20"'
    'style="margin-left: 20px;"' = 'class="margin-left-20"'
    'style="margin-left: 12px;"' = 'class="margin-left-12"'
    'style="margin-left: 10px;"' = 'class="margin-left-10"'
    'style="flex: 1; max-width: 350px;"' = 'class="flex-1 max-width-350"'
    'style="flex: 1; max-width: 100px;"' = 'class="flex-1 max-width-100"'
    'style="margin-left: 12px; flex-shrink: 0;"' = 'class="margin-left-12 flex-shrink-0"'
    'style="align-items: center;"' = 'class="align-center"'
    'style="border:none; box-shadow:var(--shadow-lg);"' = 'class="panel-no-border"'
    'style="color:var(--text-secondary); padding:20px; text-align:center;"' = 'class="text-secondary-center"'
    'style="margin:0; font-size:16px;"' = 'class="title-h3"'
    'style="margin:0; font-size:18px;"' = 'class="title-h3-large"'
}

foreach ($key in $replacements.Keys) {
    $value = $replacements[$key]
    $content = $content -replace [regex]::Escape($key), $value
}

$content | Out-File $htmlPath -Encoding UTF8
Write-Host "Inline styles replaced successfully"
