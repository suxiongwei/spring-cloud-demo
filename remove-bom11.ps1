$filePath = 'd:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html'

Write-Host "Reading file using FileStream..."
$fs = [System.IO.File]::OpenRead($filePath)
$sr = New-Object System.IO.StreamReader($fs, [System.Text.Encoding]::UTF8)
$content = $sr.ReadToEnd()
$sr.Close()
$fs.Close()

Write-Host "Checking for BOM character..."
$firstChar = $content[0]
$firstCharCode = [int][char]$firstChar
Write-Host "First character code: $firstCharCode"

if ($firstCharCode -eq 65279) {
    Write-Host "BOM character detected!"
    Write-Host "Removing BOM character..."
    $content = $content.Substring(1)
    
    Write-Host "Writing back using FileStream..."
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    $fsOut = [System.IO.File]::Open($filePath, [System.IO.FileMode]::Create, [System.IO.FileAccess]::Write)
    $sw = New-Object System.IO.StreamWriter($fsOut, $utf8NoBom)
    $sw.Write($content)
    $sw.Flush()
    $sw.Close()
    $fsOut.Close()
    
    Write-Host "Verifying..."
    $fsCheck = [System.IO.File]::OpenRead($filePath)
    $srCheck = New-Object System.IO.StreamReader($fsCheck, [System.Text.Encoding]::UTF8)
    $checkContent = $srCheck.ReadToEnd()
    $srCheck.Close()
    $fsCheck.Close()
    
    $checkFirstChar = $checkContent[0]
    $checkFirstCharCode = [int][char]$checkFirstChar
    Write-Host "New first character code: $checkFirstCharCode"
    
    if ($checkFirstCharCode -eq 65279) {
        Write-Host "ERROR: BOM still present!"
    } else {
        Write-Host "SUCCESS: BOM removed!"
    }
} else {
    Write-Host "No BOM character detected"
}
