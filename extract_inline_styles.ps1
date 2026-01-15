$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$cssPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\css\inline-styles.css"

$htmlContent = Get-Content $htmlPath -Raw -Encoding UTF8

$pattern = 'style="([^"]*)"'
$matches = [regex]::Matches($htmlContent, $pattern)

$uniqueStyles = @{}
$classCounter = 1

foreach ($match in $matches) {
    $styleValue = $match.Groups[1].Value
    
    if (-not $uniqueStyles.ContainsKey($styleValue)) {
        $className = "inline-style-$classCounter"
        $uniqueStyles[$styleValue] = $className
        $classCounter++
    }
}

$cssContent = "/* Inline styles converted to CSS classes */`n`n"
foreach ($style in $uniqueStyles.Keys) {
    $className = $uniqueStyles[$style]
    $cssContent += ".$className {`n    $style`n}`n`n"
}

$cssContent | Out-File $cssPath -Encoding UTF8

Write-Host "Found $($uniqueStyles.Count) unique inline styles"
Write-Host "CSS classes generated and saved to inline-styles.css"
