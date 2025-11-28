# Spring Cloud Demo - Sentinel规则部署脚本 (PowerShell)
# 功能：向Nacos发布Sentinel限流、熔断、授权规则

param(
    [string]$NacosAddr = "localhost:8848",
    [string]$Namespace = "8699ba10-d5ae-4183-aa94-eef36789f4d3",
    [string]$Username = "nacos",
    [string]$Password = "nacos",
    [ValidateSet("deploy", "list")]
    [string]$Action = "deploy"
)

$ErrorActionPreference = "Stop"

# 颜色定义
$Colors = @{
    Success = "Green"
    Error = "Red"
    Warning = "Yellow"
    Info = "Cyan"
}

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$ForegroundColor = "White"
    )
    Write-Host $Message -ForegroundColor $ForegroundColor
}

function Publish-NacosConfig {
    param(
        [string]$DataId,
        [string]$Group,
        [string]$Content
    )
    
    $url = "http://$NacosAddr/nacos/v1/cs/configs"
    
    $body = @{
        dataId    = $DataId
        group     = $Group
        content   = $Content
        namespace = $Namespace
        username  = $Username
        password  = $Password
        type      = "json"
    }
    
    try {
        $response = Invoke-WebRequest -Uri $url -Method Post -Body $body -TimeoutSec 5
        if ($response.StatusCode -eq 200 -and $response.Content -eq "true") {
            Write-ColorOutput "  ✓ 配置发布成功: $DataId" $Colors.Success
            return $true
        }
        else {
            Write-ColorOutput "  ✗ 配置发布失败 ($DataId): $($response.Content)" $Colors.Error
            return $false
        }
    }
    catch {
        Write-ColorOutput "  ✗ 发布配置异常 ($DataId): $_" $Colors.Error
        return $false
    }
}

function Get-NacosConfig {
    param(
        [string]$DataId,
        [string]$Group
    )
    
    $url = "http://$NacosAddr/nacos/v1/cs/configs"
    
    $params = @{
        dataId    = $DataId
        group     = $Group
        namespace = $Namespace
        username  = $Username
        password  = $Password
    }
    
    $queryString = ($params.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join "&"
    $fullUrl = "$url`?$queryString"
    
    try {
        $response = Invoke-WebRequest -Uri $fullUrl -Method Get -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            return $response.Content
        }
        return $null
    }
    catch {
        Write-ColorOutput "  ✗ 获取配置异常 ($DataId): $_" $Colors.Error
        return $null
    }
}

function Deploy-SentinelRules {
    Write-ColorOutput "`n🚀 开始部署Sentinel规则...`n" $Colors.Info
    
    # service-product-dubbo 流控规则
    Write-ColorOutput "[1/5] 部署 service-product-dubbo 流控规则..." $Colors.Info
    $flowRules = @(
        @{
            resource             = "indi.mofan.product.dubbo.service.IProductDubboService"
            limitApp             = "default"
            grade                = 1
            count                = 100
            interval             = 1
            intervalUnit         = "SECONDS"
            refCount             = 0
            behavior             = 0
            controlBehavior      = 0
            warmUpPeriodSec      = 10
            maxQueueingTimeMs    = 500
            statIntervalMs       = 1000
            linkStrategy         = 0
            clusterMode          = $false
        }
    )
    Publish-NacosConfig `
        -DataId "service-product-dubbo-flow-rules" `
        -Group "SENTINEL_GROUP" `
        -Content (ConvertTo-Json $flowRules)
    
    # service-product-dubbo 熔断规则
    Write-ColorOutput "[2/5] 部署 service-product-dubbo 熔断规则..." $Colors.Info
    $degradeRules = @(
        @{
            resource              = "indi.mofan.product.dubbo.service.IProductDubboService"
            grade                 = 0
            count                 = 500
            timeWindow            = 30
            minRequestAmount      = 5
            statIntervalMs        = 1000
            slowRatioThreshold    = 0.5
            statistic             = "resource_name"
            strategy              = 0
        },
        @{
            resource              = "indi.mofan.product.dubbo.service.IProductDubboService"
            grade                 = 1
            count                 = 0.5
            timeWindow            = 30
            minRequestAmount      = 5
            statIntervalMs        = 1000
            slowRatioThreshold    = 0.5
            statistic             = "resource_name"
            strategy              = 0
        }
    )
    Publish-NacosConfig `
        -DataId "service-product-dubbo-degrade-rules" `
        -Group "SENTINEL_GROUP" `
        -Content (ConvertTo-Json $degradeRules)
    
    # service-product-dubbo 授权规则
    Write-ColorOutput "[3/5] 部署 service-product-dubbo 授权规则..." $Colors.Info
    $authorityRules = @(
        @{
            resource  = "indi.mofan.product.dubbo.service.IProductDubboService"
            limitApp  = "service-order-dubbo"
            strategy  = 1
        }
    )
    Publish-NacosConfig `
        -DataId "service-product-dubbo-authority-rules" `
        -Group "SENTINEL_GROUP" `
        -Content (ConvertTo-Json $authorityRules)
    
    # service-order-dubbo 流控规则
    Write-ColorOutput "[4/5] 部署 service-order-dubbo 流控规则..." $Colors.Info
    $orderFlowRules = @(
        @{
            resource             = "indi.mofan.product.dubbo.service.IProductDubboService"
            limitApp             = "default"
            grade                = 1
            count                = 50
            interval             = 1
            intervalUnit         = "SECONDS"
            refCount             = 0
            behavior             = 0
            controlBehavior      = 0
            warmUpPeriodSec      = 10
            maxQueueingTimeMs    = 500
            statIntervalMs       = 1000
            linkStrategy         = 0
            clusterMode          = $false
        }
    )
    Publish-NacosConfig `
        -DataId "service-order-dubbo-flow-rules" `
        -Group "SENTINEL_GROUP" `
        -Content (ConvertTo-Json $orderFlowRules)
    
    # service-order-dubbo 熔断规则
    Write-ColorOutput "[5/5] 部署 service-order-dubbo 熔断规则..." $Colors.Info
    $orderDegradeRules = @(
        @{
            resource              = "indi.mofan.product.dubbo.service.IProductDubboService"
            grade                 = 0
            count                 = 1000
            timeWindow            = 30
            minRequestAmount      = 5
            statIntervalMs        = 1000
            slowRatioThreshold    = 0.5
            statistic             = "resource_name"
            strategy              = 0
        }
    )
    Publish-NacosConfig `
        -DataId "service-order-dubbo-degrade-rules" `
        -Group "SENTINEL_GROUP" `
        -Content (ConvertTo-Json $orderDegradeRules)
    
    Write-ColorOutput "`n✅ 所有规则部署完成！`n" $Colors.Success
    
    Write-ColorOutput "📊 已部署的规则:" $Colors.Info
    Write-ColorOutput "  ✓ service-product-dubbo-flow-rules (限流)" $Colors.Success
    Write-ColorOutput "  ✓ service-product-dubbo-degrade-rules (熔断)" $Colors.Success
    Write-ColorOutput "  ✓ service-product-dubbo-authority-rules (授权)" $Colors.Success
    Write-ColorOutput "  ✓ service-order-dubbo-flow-rules (限流)" $Colors.Success
    Write-ColorOutput "  ✓ service-order-dubbo-degrade-rules (熔断)" $Colors.Success
    
    Write-ColorOutput "`n🔗 Sentinel Dashboard: http://localhost:8858`n" $Colors.Info
}

function List-SentinelRules {
    Write-ColorOutput "`n📋 已部署的Sentinel规则:`n" $Colors.Info
    
    $rules = @(
        "service-product-dubbo-flow-rules",
        "service-product-dubbo-degrade-rules",
        "service-product-dubbo-authority-rules",
        "service-order-dubbo-flow-rules",
        "service-order-dubbo-degrade-rules"
    )
    
    foreach ($ruleName in $rules) {
        $config = Get-NacosConfig -DataId $ruleName -Group "SENTINEL_GROUP"
        if ($config) {
            try {
                $ruleData = ConvertFrom-Json $config
                Write-ColorOutput "✓ $ruleName:" $Colors.Success
                Write-ColorOutput ($ruleData | ConvertTo-Json -Depth 10) $Colors.Info
                Write-Host ""
            }
            catch {
                Write-ColorOutput "✗ $ruleName`: 配置格式错误" $Colors.Error
            }
        }
        else {
            Write-ColorOutput "⚠ $ruleName`: 未找到" $Colors.Warning
        }
    }
}

# 主程序
Write-ColorOutput "`n╔════════════════════════════════════════╗" $Colors.Info
Write-ColorOutput "║  Sentinel 规则部署工具 (PowerShell)    ║" $Colors.Info
Write-ColorOutput "║  Nacos: $NacosAddr" $Colors.Info
Write-ColorOutput "╚════════════════════════════════════════╝" $Colors.Info

try {
    if ($Action -eq "deploy") {
        Deploy-SentinelRules
    }
    elseif ($Action -eq "list") {
        List-SentinelRules
    }
}
catch {
    Write-ColorOutput "`n❌ 发生错误: $_" $Colors.Error
    exit 1
}
