$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'

Write-Host "Reading file content..."
$content = Get-Content $filePath -Raw -Encoding UTF8

Write-Host "Checking for BOM character at the start..."
$firstChar = $content[0]
$firstCharCode = [int][char]$firstChar
Write-Host "First character code: $firstCharCode"

if ($firstCharCode -eq 65279) {
    Write-Host "BOM character (U+FEFF) detected!"
    Write-Host "Removing BOM character..."
    $content = $content.Substring(1)
    
    Write-Host "Writing back to file..."
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)
    
    Write-Host "Verifying..."
    $verifyContent = Get-Content $filePath -Raw -Encoding UTF8
    $verifyFirstChar = $verifyContent[0]
    $verifyFirstCharCode = [int][char]$verifyFirstChar
    Write-Host "New first character code: $verifyFirstCharCode"
    
    if ($verifyFirstCharCode -eq 65279) {
        Write-Host "ERROR: BOM still present!"
    } else {
        Write-Host "SUCCESS: BOM removed!"
    }
} else {
    Write-Host "No BOM character detected at the start"
}
