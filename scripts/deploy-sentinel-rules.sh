#!/bin/bash

# Spring Cloud Demo - Sentinel规则部署脚本 (Bash)
# 功能：向Nacos发布Sentinel限流、熔断、授权规则

set -e

# 默认参数
NACOS_ADDR="${1:-localhost:8848}"
NAMESPACE="${2:-8699ba10-d5ae-4183-aa94-eef36789f4d3}"
USERNAME="${3:-nacos}"
PASSWORD="${4:-nacos}"
ACTION="${5:-deploy}"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# 函数：显示彩色输出
print_color() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 函数：发布配置到Nacos
publish_nacos_config() {
    local data_id=$1
    local group=$2
    local content=$3
    
    local url="http://${NACOS_ADDR}/nacos/v1/cs/configs"
    
    # URL编码content
    local encoded_content=$(echo -n "$content" | jq -sRr @uri)
    
    # 发送POST请求
    local response=$(curl -s -X POST "$url" \
        -d "dataId=$data_id" \
        -d "group=$group" \
        -d "content=$content" \
        -d "namespace=$NAMESPACE" \
        -d "username=$USERNAME" \
        -d "password=$PASSWORD" \
        -d "type=json")
    
    if [ "$response" = "true" ]; then
        print_color "$GREEN" "  ✓ 配置发布成功: $data_id"
        return 0
    else
        print_color "$RED" "  ✗ 配置发布失败 ($data_id): $response"
        return 1
    fi
}

# 函数：获取Nacos配置
get_nacos_config() {
    local data_id=$1
    local group=$2
    
    local url="http://${NACOS_ADDR}/nacos/v1/cs/configs"
    local params="?dataId=$data_id&group=$group&namespace=$NAMESPACE&username=$USERNAME&password=$PASSWORD"
    
    curl -s "$url$params" 2>/dev/null || echo ""
}

# 函数：部署规则
deploy_sentinel_rules() {
    print_color "$CYAN" "\n🚀 开始部署Sentinel规则...\n"
    
    # service-product-dubbo 流控规则
    print_color "$CYAN" "[1/5] 部署 service-product-dubbo 流控规则..."
    local flow_rules='[
        {
            "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
            "limitApp": "default",
            "grade": 1,
            "count": 100,
            "interval": 1,
            "intervalUnit": "SECONDS",
            "refCount": 0,
            "behavior": 0,
            "controlBehavior": 0,
            "warmUpPeriodSec": 10,
            "maxQueueingTimeMs": 500,
            "statIntervalMs": 1000,
            "linkStrategy": 0,
            "clusterMode": false
        }
    ]'
    publish_nacos_config "service-product-dubbo-flow-rules" "SENTINEL_GROUP" "$flow_rules"
    
    # service-product-dubbo 熔断规则
    print_color "$CYAN" "[2/5] 部署 service-product-dubbo 熔断规则..."
    local degrade_rules='[
        {
            "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
            "grade": 0,
            "count": 500,
            "timeWindow": 30,
            "minRequestAmount": 5,
            "statIntervalMs": 1000,
            "slowRatioThreshold": 0.5,
            "statistic": "resource_name",
            "strategy": 0
        },
        {
            "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
            "grade": 1,
            "count": 0.5,
            "timeWindow": 30,
            "minRequestAmount": 5,
            "statIntervalMs": 1000,
            "slowRatioThreshold": 0.5,
            "statistic": "resource_name",
            "strategy": 0
        }
    ]'
    publish_nacos_config "service-product-dubbo-degrade-rules" "SENTINEL_GROUP" "$degrade_rules"
    
    # service-product-dubbo 授权规则
    print_color "$CYAN" "[3/5] 部署 service-product-dubbo 授权规则..."
    local authority_rules='[
        {
            "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
            "limitApp": "service-order-dubbo",
            "strategy": 1
        }
    ]'
    publish_nacos_config "service-product-dubbo-authority-rules" "SENTINEL_GROUP" "$authority_rules"
    
    # service-order-dubbo 流控规则
    print_color "$CYAN" "[4/5] 部署 service-order-dubbo 流控规则..."
    local order_flow_rules='[
        {
            "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
            "limitApp": "default",
            "grade": 1,
            "count": 50,
            "interval": 1,
            "intervalUnit": "SECONDS",
            "refCount": 0,
            "behavior": 0,
            "controlBehavior": 0,
            "warmUpPeriodSec": 10,
            "maxQueueingTimeMs": 500,
            "statIntervalMs": 1000,
            "linkStrategy": 0,
            "clusterMode": false
        }
    ]'
    publish_nacos_config "service-order-dubbo-flow-rules" "SENTINEL_GROUP" "$order_flow_rules"
    
    # service-order-dubbo 熔断规则
    print_color "$CYAN" "[5/5] 部署 service-order-dubbo 熔断规则..."
    local order_degrade_rules='[
        {
            "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
            "grade": 0,
            "count": 1000,
            "timeWindow": 30,
            "minRequestAmount": 5,
            "statIntervalMs": 1000,
            "slowRatioThreshold": 0.5,
            "statistic": "resource_name",
            "strategy": 0
        }
    ]'
    publish_nacos_config "service-order-dubbo-degrade-rules" "SENTINEL_GROUP" "$order_degrade_rules"
    
    print_color "$GREEN" "\n✅ 所有规则部署完成！\n"
    
    print_color "$CYAN" "📊 已部署的规则:"
    print_color "$GREEN" "  ✓ service-product-dubbo-flow-rules (限流)"
    print_color "$GREEN" "  ✓ service-product-dubbo-degrade-rules (熔断)"
    print_color "$GREEN" "  ✓ service-product-dubbo-authority-rules (授权)"
    print_color "$GREEN" "  ✓ service-order-dubbo-flow-rules (限流)"
    print_color "$GREEN" "  ✓ service-order-dubbo-degrade-rules (熔断)"
    
    print_color "$CYAN" "\n🔗 Sentinel Dashboard: http://localhost:8858\n"
}

# 函数：列出规则
list_sentinel_rules() {
    print_color "$CYAN" "\n📋 已部署的Sentinel规则:\n"
    
    local rules=(
        "service-product-dubbo-flow-rules"
        "service-product-dubbo-degrade-rules"
        "service-product-dubbo-authority-rules"
        "service-order-dubbo-flow-rules"
        "service-order-dubbo-degrade-rules"
    )
    
    for rule_name in "${rules[@]}"; do
        local config=$(get_nacos_config "$rule_name" "SENTINEL_GROUP")
        if [ -n "$config" ]; then
            print_color "$GREEN" "✓ $rule_name:"
            echo "$config" | jq '.' 2>/dev/null || echo "$config"
            echo ""
        else
            print_color "$YELLOW" "⚠ $rule_name: 未找到"
        fi
    done
}

# 主程序
print_color "$CYAN" "\n╔════════════════════════════════════════╗"
print_color "$CYAN" "║  Sentinel 规则部署工具 (Bash)         ║"
print_color "$CYAN" "║  Nacos: $NACOS_ADDR"
print_color "$CYAN" "╚════════════════════════════════════════╝"

if [ "$ACTION" = "deploy" ]; then
    deploy_sentinel_rules
elif [ "$ACTION" = "list" ]; then
    list_sentinel_rules
else
    print_color "$RED" "\n❌ 未知的操作: $ACTION"
    print_color "$CYAN" "用法: $0 [nacos_addr] [namespace] [username] [password] [deploy|list]"
    exit 1
fi
