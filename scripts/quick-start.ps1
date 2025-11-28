# Spring Cloud Demo - 一键部署脚本 (PowerShell)
# 用途：启动所有服务并部署Sentinel规则

$ErrorActionPreference = "Stop"

# 颜色定义
$Colors = @{
    Success = "Green"
    Error   = "Red"
    Warning = "Yellow"
    Info    = "Cyan"
}

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$ForegroundColor = "White"
    )
    Write-Host $Message -ForegroundColor $ForegroundColor
}

Write-ColorOutput "`n╔════════════════════════════════════════╗" $Colors.Info
Write-ColorOutput "║  Spring Cloud Demo 一键启动部署脚本   ║" $Colors.Info
Write-ColorOutput "╚════════════════════════════════════════╝`n" $Colors.Info

try {
    # 1. 启动Docker
    Write-ColorOutput "[1/3] 启动Docker容器..." $Colors.Info
    docker-compose up -d
    
    Write-ColorOutput "等待Nacos启动..." $Colors.Info
    Start-Sleep -Seconds 15

    # 2. 部署Sentinel规则
    Write-ColorOutput "`n[2/3] 部署Sentinel限流规则..." $Colors.Info
    & ".\scripts\deploy-sentinel-rules.ps1"

    # 3. 显示服务信息
    Write-ColorOutput "`n[3/3] 启动完成！`n" $Colors.Success
    docker-compose ps

    Write-ColorOutput "`n╔════════════════════════════════════════╗" $Colors.Success
    Write-ColorOutput "║       🎉 所有服务已启动！              ║" $Colors.Success
    Write-ColorOutput "╚════════════════════════════════════════╝`n" $Colors.Success

    Write-ColorOutput "📊 服务地址:" $Colors.Info
    Write-ColorOutput "  Gateway:      http://localhost:8080" $Colors.Success
    Write-ColorOutput "  Nacos:        http://localhost:8848/nacos (nacos/nacos)" $Colors.Success
    Write-ColorOutput "  Sentinel:     http://localhost:8858" $Colors.Success

    Write-ColorOutput "`n📦 Feign服务 (HTTP):" $Colors.Info
    Write-ColorOutput "  Product:      http://localhost:8010" $Colors.Success
    Write-ColorOutput "  Order:        http://localhost:8000" $Colors.Success

    Write-ColorOutput "`n🚀 Dubbo服务 (RPC):" $Colors.Info
    Write-ColorOutput "  Product:      http://localhost:8011 (TCP: 20881)" $Colors.Success
    Write-ColorOutput "  Order:        http://localhost:8001 (TCP: 20880)" $Colors.Success

    Write-ColorOutput "`n💡 快速测试:" $Colors.Info
    Write-ColorOutput "  Invoke-WebRequest http://localhost:8080/api/order/dubbo/call-sync?productId=1`n" $Colors.Info
}
catch {
    Write-ColorOutput "`n❌ 发生错误: $_" $Colors.Error
    exit 1
}
