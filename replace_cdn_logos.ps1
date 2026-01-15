$htmlPath = "d:\project\spring-cloud-demo\gateway\src\main\resources\static\service-demo.html"
$content = Get-Content $htmlPath -Raw -Encoding UTF8

$replacements = @{
    'https://img.alicdn.com/imgextra/i3/O1CN01Mi9RDw1wFphKfvLxV_!!6000000006279-55-tps-29-14.svg' = 'images/logos/nacos-aliyun.svg'
    'https://img.alicdn.com/imgextra/i3/O1CN01Z19f2d1X38JuBVfLX_!!6000000002867-55-tps-22-33.svg' = 'images/logos/sentinel-aliyun.svg'
    'https://img.alicdn.com/imgextra/i1/O1CN01Bgku601IaMpCoTO7l_!!6000000000909-55-tps-23-27.svg' = 'images/logos/dubbo-aliyun.svg'
    'https://img.alicdn.com/imgextra/i2/O1CN014OFGKB1PjFoyRkUVK_!!6000000001876-55-tps-28-28.svg' = 'images/logos/spring-cloud-aliyun.svg'
    'https://img.alicdn.com/imgextra/i3/O1CN015oej0c1DwHiK9gS2H_!!6000000000280-55-tps-26-36.svg' = 'images/logos/rocketmq-aliyun.svg'
    'https://img.alicdn.com/imgextra/i2/O1CN01gTVczm23JDwHnxg2U_!!6000000007234-55-tps-20-30.svg' = 'images/logos/seata-aliyun.svg'
}

foreach ($key in $replacements.Keys) {
    $value = $replacements[$key]
    $content = $content -replace [regex]::Escape($key), $value
    Write-Host "Replaced: $key -> $value"
}

Set-Content $htmlPath -Value $content -Encoding UTF8 -NoNewline
Write-Host "Logo image replacements completed!"
