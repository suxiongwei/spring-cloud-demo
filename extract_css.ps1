$filePath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $filePath -Raw -Encoding UTF8

$pattern = '<style>(.*?)</style>'
$match = [regex]::Match($content, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)

if ($match.Success) {
    $cssContent = $match.Groups[1].Value.Trim()
    $cssContent | Out-File "d:\project\spring-cloud-demo\gateway\src\main\resources\static\css\main.css" -Encoding UTF8
    Write-Host "CSS extracted successfully to main.css"
} else {
    Write-Host "No <style> tag found"
}
