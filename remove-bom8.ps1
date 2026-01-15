$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'
$bytes = [System.IO.File]::ReadAllBytes($filePath)
$firstThreeBytes = $bytes[0..2] -join ','
Write-Host "First three bytes: $firstThreeBytes"

if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
    Write-Host "BOM detected (EF BB BF)"
    $newBytes = New-Object byte[] ($bytes.Length - 3)
    [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
    [System.IO.File]::WriteAllBytes($filePath, $newBytes)
    Write-Host "BOM removed"
    
    $newBytesCheck = [System.IO.File]::ReadAllBytes($filePath)
    $newFirstThreeBytes = $newBytesCheck[0..2] -join ','
    Write-Host "New first three bytes: $newFirstThreeBytes"
} else {
    Write-Host "No UTF-8 BOM detected"
}
