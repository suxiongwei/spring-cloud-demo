$bytes = [System.IO.File]::ReadAllBytes('d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html')
if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
    $bytes = $bytes[3..($bytes.Length-1)]
}
[System.IO.File]::WriteAllBytes('d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html', $bytes)
