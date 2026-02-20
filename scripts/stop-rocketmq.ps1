$ErrorActionPreference = "Stop"
$composeFile = "docker-compose.rocketmq.yml"

if (-not (Test-Path $composeFile)) {
    throw "Compose file not found: $composeFile"
}

Write-Host "Stopping RocketMQ stack ..."
docker compose -f $composeFile down

Write-Host ""
docker compose -f $composeFile ps
