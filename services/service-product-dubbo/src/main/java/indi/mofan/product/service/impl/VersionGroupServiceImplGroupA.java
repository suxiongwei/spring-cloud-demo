package indi.mofan.product.service.impl;

import indi.mofan.product.dubbo.service.IVersionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService(interfaceClass = IVersionGroupService.class, version = "1.0.0", group = "groupA", protocol = { "dubbo",
        "tri" })
public class VersionGroupServiceImplGroupA implements IVersionGroupService {

    @Override
    public Map<String, Object> sayHello(String name) {
        log.info("Group A Version 1.0.0 - sayHello called with name: {}", name);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello " + name + " from Group A Version 1.0.0");
        result.put("version", "1.0.0");
        result.put("group", "groupA");
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("group", "groupA");
        info.put("description", "这是分组 A 版本 1.0.0 的服务实现");
        info.put("features", new String[] { "分组A专用功能", "环境隔离" });
        info.put("timestamp", System.currentTimeMillis());

        log.info("Group A Version 1.0.0 - getServerInfo called");
        return info;
    }

    @Override
    public List<Map<String, Object>> getMenuItems() {
        log.info("Group A Version 1.0.0 - getMenuItems called");

        List<Map<String, Object>> menuItems = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 3);
        item1.put("name", "订单管理");
        item1.put("path", "/orders");
        item1.put("group", "groupA");
        menuItems.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 4);
        item2.put("name", "商品管理");
        item2.put("path", "/products");
        item2.put("group", "groupA");
        menuItems.add(item2);

        return menuItems;
    }
}
