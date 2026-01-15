$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'
$tempPath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo-temp.html'

Write-Host "Checking file status..."

try {
    $stream = [System.IO.File]::Open($filePath, [System.IO.FileMode]::Open, [System.IO.FileAccess]::ReadWrite, [System.IO.FileShare]::None)
    $stream.Close()
    Write-Host "File is not locked"
} catch {
    Write-Host "File is locked by another process: $_"
    Write-Host "Please close the file in your IDE and try again."
    exit 1
}

$bytes = [System.IO.File]::ReadAllBytes($filePath)
$firstThreeBytes = $bytes[0..2] -join ','
Write-Host "First three bytes: $firstThreeBytes"

if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
    Write-Host "BOM detected (EF BB BF)"
    $newBytes = New-Object byte[] ($bytes.Length - 3)
    [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
    
    Write-Host "Writing to temporary file..."
    [System.IO.File]::WriteAllBytes($tempPath, $newBytes)
    
    Write-Host "Replacing original file..."
    [System.IO.File]::Delete($filePath)
    [System.IO.File]::Move($tempPath, $filePath)
    
    Write-Host "Verifying BOM removal..."
    $verifyBytes = [System.IO.File]::ReadAllBytes($filePath)
    $verifyFirstThreeBytes = $verifyBytes[0..2] -join ','
    Write-Host "New first three bytes: $verifyFirstThreeBytes"
    
    if ($verifyBytes.Length -ge 3 -and $verifyBytes[0] -eq 0xEF -and $verifyBytes[1] -eq 0xBB -and $verifyBytes[2] -eq 0xBF) {
        Write-Host "ERROR: BOM still present!"
    } else {
        Write-Host "SUCCESS: BOM removed!"
    }
} else {
    Write-Host "No UTF-8 BOM detected"
}
