$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'
$tempPath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo-temp.html'

$reader = New-Object System.IO.StreamReader($filePath, [System.Text.Encoding]::UTF8)
$content = $reader.ReadToEnd()
$reader.Close()

$writer = New-Object System.IO.StreamWriter($tempPath, $false, [System.Text.Encoding]::UTF8)
$writer.Write($content)
$writer.Close()

Copy-Item -Force $tempPath $filePath
Remove-Item $tempPath
Write-Host "File rewritten without BOM"
