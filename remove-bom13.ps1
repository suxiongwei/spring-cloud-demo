$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'

Write-Host "Reading file as bytes..."
$bytes = [System.IO.File]::ReadAllBytes($filePath)
Write-Host "File size: $($bytes.Length) bytes"

Write-Host "First 20 bytes: $($bytes[0..19] -join ',')"

Write-Host "Counting BOM sequences at the start..."
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
    
    Write-Host "New file size: $($newBytes.Length) bytes"
    Write-Host "New first 20 bytes: $($newBytes[0..19] -join ',')"
    
    Write-Host "Writing to file..."
    [System.IO.File]::WriteAllBytes($filePath, $newBytes)
    
    Write-Host "Verifying..."
    $verifyBytes = [System.IO.File]::ReadAllBytes($filePath)
    Write-Host "Verified file size: $($verifyBytes.Length) bytes"
    Write-Host "Verified first 20 bytes: $($verifyBytes[0..19] -join ',')"
    
    $verifyBomCount = 0
    $verifyIndex = 0
    while ($verifyIndex + 2 -lt $verifyBytes.Length -and $verifyBytes[$verifyIndex] -eq 0xEF -and $verifyBytes[$verifyIndex + 1] -eq 0xBB -and $verifyBytes[$verifyIndex + 2] -eq 0xBF) {
        $verifyBomCount++
        $verifyIndex += 3
    }
    
    if ($verifyBomCount -gt 0) {
        Write-Host "ERROR: Still have $verifyBomCount BOM sequences!"
    } else {
        Write-Host "SUCCESS: All BOM sequences removed!"
    }
} else {
    Write-Host "No BOM sequences detected"
}
