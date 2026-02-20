param(
    [string]$BrokerIp1 = "127.0.0.1"
)

$ErrorActionPreference = "Stop"
$composeFile = "docker-compose.rocketmq.yml"

if (-not (Test-Path $composeFile)) {
    throw "Compose file not found: $composeFile"
}

$env:BROKER_IP1 = $BrokerIp1
Write-Host "Starting RocketMQ stack with BROKER_IP1=$BrokerIp1 ..."
docker compose -f $composeFile up -d

Start-Sleep -Seconds 8
$running = @(docker compose -f $composeFile ps --services --filter status=running)

Write-Host ""
docker compose -f $composeFile ps

if ($running -notcontains "rocketmq-namesrv" -or $running -notcontains "rocketmq-broker") {
    Write-Warning "RocketMQ stack did not fully start. Showing broker logs:"
    docker compose -f $composeFile logs rocketmq-broker --tail=60
    throw "RocketMQ startup check failed."
}

Write-Host ""
Write-Host "RocketMQ started."
Write-Host "NameServer: 127.0.0.1:9876"
Write-Host "Broker: 127.0.0.1:10911"
Write-Host "To stop: powershell -ExecutionPolicy Bypass -File scripts/stop-rocketmq.ps1"
