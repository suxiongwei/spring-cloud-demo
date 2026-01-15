$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$replacements = @{
    '🌙' = '<img src="images/icons/moon.svg" class="icon" alt="Dark Mode">'
    '☀️' = '<img src="images/icons/sun.svg" class="icon" alt="Light Mode">'
    '🔍' = '<img src="images/icons/search.svg" class="icon" alt="Search">'
    '⚙️' = '<img src="images/icons/settings.svg" class="icon" alt="Settings">'
    '⏱️' = '<img src="images/icons/clock.svg" class="icon" alt="Clock">'
    '❌' = '<img src="images/icons/close.svg" class="icon" alt="Close">'
    '🔒' = '<img src="images/icons/lock.svg" class="icon" alt="Lock">'
    '⚖️' = '<img src="images/icons/balance.svg" class="icon" alt="Balance">'
    '🏷️' = '<img src="images/icons/tag.svg" class="icon" alt="Tag">'
    '📊' = '<img src="images/icons/chart.svg" class="icon" alt="Chart">'
    '❤️' = '<img src="images/icons/heart.svg" class="icon" alt="Heart">'
    '✅' = '<img src="images/icons/check.svg" class="icon" alt="Check">'
    '💡' = '<img src="images/icons/lightbulb.svg" class="icon" alt="Lightbulb">'
    '🗑️' = '<img src="images/icons/trash.svg" class="icon" alt="Trash">'
}

foreach ($key in $replacements.Keys) {
    $value = $replacements[$key]
    $content = $content -replace [regex]::Escape($key), $value
    Write-Host "Replaced: $key -> $value"
}

Set-Content $htmlPath -Value $content -Encoding UTF8 -NoNewline
Write-Host "Emoji replacements completed!"
