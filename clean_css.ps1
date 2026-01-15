$cssPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\css\main.css"
$cssContent = Get-Content $cssPath -Raw -Encoding UTF8

$cleanedCss = $cssContent -replace '^\s{8}', ''
$cleanedCss = $cleanedCss -replace '\n\s{8}', "`n"
$cleanedCss = $cleanedCss.Trim()

$cleanedCss | Out-File $cssPath -Encoding UTF8
Write-Host "CSS cleaned successfully"
