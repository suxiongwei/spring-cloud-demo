$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'

Write-Host "Reading file as bytes..."
$bytes = [System.IO.File]::ReadAllBytes($filePath)
Write-Host "File size: $($bytes.Length) bytes"

Write-Host "First 10 bytes: $($bytes[0..9] -join ',')"

if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
    Write-Host "BOM detected (EF BB BF)"
    
    Write-Host "Creating new byte array without BOM..."
    $newBytes = New-Object byte[] ($bytes.Length - 3)
    [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
    
    Write-Host "New file size: $($newBytes.Length) bytes"
    Write-Host "New first 10 bytes: $($newBytes[0..9] -join ',')"
    
    Write-Host "Writing to file..."
    [System.IO.File]::WriteAllBytes($filePath, $newBytes)
    
    Write-Host "Verifying..."
    $verifyBytes = [System.IO.File]::ReadAllBytes($filePath)
    Write-Host "Verified file size: $($verifyBytes.Length) bytes"
    Write-Host "Verified first 10 bytes: $($verifyBytes[0..9] -join ',')"
    
    if ($verifyBytes.Length -ge 3 -and $verifyBytes[0] -eq 0xEF -and $verifyBytes[1] -eq 0xBB -and $verifyBytes[2] -eq 0xBF) {
        Write-Host "ERROR: BOM still present!"
    } else {
        Write-Host "SUCCESS: BOM removed!"
    }
} else {
    Write-Host "No UTF-8 BOM detected"
}
