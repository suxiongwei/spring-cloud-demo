$content = Get-Content 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html' -Raw -Encoding UTF8
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText('d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo-nobom.html', $content, $utf8NoBom)
Copy-Item -Force 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo-nobom.html' 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'
Remove-Item 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo-nobom.html'
