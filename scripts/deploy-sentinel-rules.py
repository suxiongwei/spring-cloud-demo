#!/usr/bin/env python3
"""
Spring Cloud Demo - Nacos Sentinel 规则配置管理工具
功能：自动创建和管理Nacos中的Sentinel限流、熔断、授权规则
"""

import json
import requests
import sys
import argparse
from typing import Dict, List

class NacosConfigManager:
    def __init__(self, nacos_addr: str, namespace: str, username: str, password: str):
        self.nacos_addr = nacos_addr
        self.namespace = namespace
        self.username = username
        self.password = password
        self.base_url = f"http://{nacos_addr}/nacos/v1/cs/configs"
        
    def publish_config(self, data_id: str, group: str, content: str) -> bool:
        """发布配置到Nacos"""
        params = {
            'dataId': data_id,
            'group': group,
            'content': content,
            'namespace': self.namespace,
            'username': self.username,
            'password': self.password,
            'type': 'json'
        }
        
        try:
            resp = requests.post(self.base_url, data=params, timeout=5)
            if resp.status_code == 200 and resp.text == 'true':
                print(f"✓ 配置发布成功: {data_id}")
                return True
            else:
                print(f"✗ 配置发布失败 ({data_id}): {resp.text}")
                return False
        except Exception as e:
            print(f"✗ 发布配置异常 ({data_id}): {str(e)}")
            return False
    
    def get_config(self, data_id: str, group: str) -> str:
        """获取Nacos配置"""
        params = {
            'dataId': data_id,
            'group': group,
            'namespace': self.namespace,
            'username': self.username,
            'password': self.password
        }
        
        try:
            resp = requests.get(self.base_url, params=params, timeout=5)
            if resp.status_code == 200:
                return resp.text
            else:
                return None
        except Exception as e:
            print(f"✗ 获取配置异常 ({data_id}): {str(e)}")
            return None

class SentinelRuleBuilder:
    """Sentinel规则构建器"""
    
    @staticmethod
    def build_dubbo_flow_rules(service_name: str) -> List[Dict]:
        """
        构建Dubbo服务流控规则
        限制调用QPS为100/s
        """
        return [
            {
                "resource": f"indi.mofan.product.dubbo.service.IProductDubboService",
                "limitApp": "default",
                "grade": 1,  # 1: QPS, 0: Thread
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
                "clusterMode": False,
                "clusterConfig": None
            }
        ]
    
    @staticmethod
    def build_dubbo_degrade_rules(service_name: str) -> List[Dict]:
        """
        构建Dubbo服务熔断降级规则
        响应时间超过500ms或错误率超过50%时进行熔断，持续30秒
        """
        return [
            {
                "resource": f"indi.mofan.product.dubbo.service.IProductDubboService",
                "grade": 0,  # 0: RT(响应时间), 1: 异常比例, 2: 异常数
                "count": 500,  # 响应时间阈值(ms)
                "timeWindow": 30,  # 熔断时间窗口(秒)
                "minRequestAmount": 5,
                "statIntervalMs": 1000,
                "slowRatioThreshold": 0.5,
                "statistic": "resource_name",
                "strategy": 0
            },
            {
                "resource": f"indi.mofan.product.dubbo.service.IProductDubboService",
                "grade": 1,  # 异常比例
                "count": 0.5,  # 50% 异常比例
                "timeWindow": 30,
                "minRequestAmount": 5,
                "statIntervalMs": 1000,
                "slowRatioThreshold": 0.5,
                "statistic": "resource_name",
                "strategy": 0
            }
        ]
    
    @staticmethod
    def build_dubbo_authority_rules(service_name: str) -> List[Dict]:
        """
        构建Dubbo服务授权规则
        仅允许来自service-order-dubbo的访问
        """
        return [
            {
                "resource": f"indi.mofan.product.dubbo.service.IProductDubboService",
                "limitApp": "service-order-dubbo",
                "strategy": 1  # 1: 白名单, 0: 黑名单
            }
        ]
    
    @staticmethod
    def build_order_flow_rules() -> List[Dict]:
        """
        构建order-dubbo流控规则
        限制Dubbo调用QPS为50/s
        """
        return [
            {
                "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
                "limitApp": "default",
                "grade": 1,  # QPS
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
                "clusterMode": False
            }
        ]
    
    @staticmethod
    def build_order_degrade_rules() -> List[Dict]:
        """
        构建order-dubbo熔断降级规则
        """
        return [
            {
                "resource": "indi.mofan.product.dubbo.service.IProductDubboService",
                "grade": 0,  # RT
                "count": 1000,  # 响应时间1秒
                "timeWindow": 30,
                "minRequestAmount": 5,
                "statIntervalMs": 1000,
                "slowRatioThreshold": 0.5,
                "statistic": "resource_name",
                "strategy": 0
            }
        ]

def deploy_sentinel_rules(nacos_addr: str, namespace: str, username: str, password: str):
    """部署所有Sentinel规则"""
    
    manager = NacosConfigManager(nacos_addr, namespace, username, password)
    builder = SentinelRuleBuilder()
    
    print("\n🚀 开始部署Sentinel规则...\n")
    
    # 部署 service-product-dubbo 规则
    print("[1/6] 部署 service-product-dubbo 流控规则...")
    flow_rules = builder.build_dubbo_flow_rules("service-product-dubbo")
    manager.publish_config(
        "service-product-dubbo-flow-rules",
        "SENTINEL_GROUP",
        json.dumps(flow_rules, indent=2)
    )
    
    print("[2/6] 部署 service-product-dubbo 熔断规则...")
    degrade_rules = builder.build_dubbo_degrade_rules("service-product-dubbo")
    manager.publish_config(
        "service-product-dubbo-degrade-rules",
        "SENTINEL_GROUP",
        json.dumps(degrade_rules, indent=2)
    )
    
    print("[3/6] 部署 service-product-dubbo 授权规则...")
    authority_rules = builder.build_dubbo_authority_rules("service-product-dubbo")
    manager.publish_config(
        "service-product-dubbo-authority-rules",
        "SENTINEL_GROUP",
        json.dumps(authority_rules, indent=2)
    )
    
    # 部署 service-order-dubbo 规则
    print("[4/6] 部署 service-order-dubbo 流控规则...")
    order_flow = builder.build_order_flow_rules()
    manager.publish_config(
        "service-order-dubbo-flow-rules",
        "SENTINEL_GROUP",
        json.dumps(order_flow, indent=2)
    )
    
    print("[5/6] 部署 service-order-dubbo 熔断规则...")
    order_degrade = builder.build_order_degrade_rules()
    manager.publish_config(
        "service-order-dubbo-degrade-rules",
        "SENTINEL_GROUP",
        json.dumps(order_degrade, indent=2)
    )
    
    print("[6/6] 完成规则部署\n")
    
    print("✅ 所有规则部署完成！\n")
    print("📊 已部署的规则:")
    print("  ✓ service-product-dubbo-flow-rules (限流)")
    print("  ✓ service-product-dubbo-degrade-rules (熔断)")
    print("  ✓ service-product-dubbo-authority-rules (授权)")
    print("  ✓ service-order-dubbo-flow-rules (限流)")
    print("  ✓ service-order-dubbo-degrade-rules (熔断)\n")
    
    print("🔗 Sentinel Dashboard: http://localhost:8858\n")

def list_sentinel_rules(nacos_addr: str, namespace: str, username: str, password: str):
    """列出已部署的Sentinel规则"""
    
    manager = NacosConfigManager(nacos_addr, namespace, username, password)
    
    print("\n📋 已部署的Sentinel规则:\n")
    
    rules = [
        "service-product-dubbo-flow-rules",
        "service-product-dubbo-degrade-rules",
        "service-product-dubbo-authority-rules",
        "service-order-dubbo-flow-rules",
        "service-order-dubbo-degrade-rules"
    ]
    
    for rule_name in rules:
        config = manager.get_config(rule_name, "SENTINEL_GROUP")
        if config:
            try:
                rule_data = json.loads(config)
                print(f"✓ {rule_name}:")
                print(f"  {json.dumps(rule_data, indent=4)}\n")
            except json.JSONDecodeError:
                print(f"✗ {rule_name}: 配置格式错误\n")
        else:
            print(f"⚠ {rule_name}: 未找到\n")

def main():
    parser = argparse.ArgumentParser(description="Nacos Sentinel 规则配置工具")
    parser.add_argument("--nacos-addr", default="localhost:8848", help="Nacos服务地址")
    parser.add_argument("--namespace", default="8699ba10-d5ae-4183-aa94-eef36789f4d3", help="命名空间")
    parser.add_argument("--username", default="nacos", help="用户名")
    parser.add_argument("--password", default="nacos", help="密码")
    parser.add_argument("--action", choices=["deploy", "list"], default="deploy", help="操作类型")
    
    args = parser.parse_args()
    
    try:
        if args.action == "deploy":
            deploy_sentinel_rules(args.nacos_addr, args.namespace, args.username, args.password)
        elif args.action == "list":
            list_sentinel_rules(args.nacos_addr, args.namespace, args.username, args.password)
    except Exception as e:
        print(f"\n❌ 发生错误: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()
