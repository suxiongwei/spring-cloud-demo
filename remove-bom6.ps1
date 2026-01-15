$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'
$bytes = [System.IO.File]::ReadAllBytes($filePath)
if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
    $newBytes = New-Object byte[] ($bytes.Length - 3)
    [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
    [System.IO.File]::WriteAllBytes($filePath, $newBytes)
    Write-Host "BOM removed successfully"
} else {
    Write-Host "No BOM found"
}
