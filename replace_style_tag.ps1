$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$pattern = '(?s)<style>.*?</style>'
$replacement = '<link rel="stylesheet" href="css/main.css">' + "`n" + '    <link rel="stylesheet" href="css/inline-styles.css">'

$content = $content -replace $pattern, $replacement

$content | Out-File $htmlPath -Encoding UTF8
Write-Host "Style tag removed and external CSS links added successfully"
