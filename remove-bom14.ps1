$filePath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"

Write-Host "Reading file: $filePath"

$bytes = [System.IO.File]::ReadAllBytes($filePath)

$bomCount = 0
$index = 0
while ($index + 2 -lt $bytes.Length -and $bytes[$index] -eq 0xEF -and $bytes[$index + 1] -eq 0xBB -and $bytes[$index + 2] -eq 0xBF) {
    $bomCount++
    $index += 3
}

Write-Host "Found $bomCount BOM sequences at the start"

if ($bomCount -gt 0) {
    Write-Host "Removing all $bomCount BOM sequences..."
    $newBytes = New-Object byte[] ($bytes.Length - ($bomCount * 3))
    [Array]::Copy($bytes, $bomCount * 3, $newBytes, 0, $bytes.Length - ($bomCount * 3))
    
    $tempPath = $filePath + ".tmp"
    [System.IO.File]::WriteAllBytes($tempPath, $newBytes)
    
    Write-Host "Replacing original file..."
    Move-Item -Path $tempPath -Destination $filePath -Force
    
    Write-Host "Done! Removed $bomCount BOM sequences."
    Write-Host "File now starts with: $([System.Text.Encoding]::UTF8.GetString($newBytes[0..20]))"
} else {
    Write-Host "No BOM sequences found."
}
