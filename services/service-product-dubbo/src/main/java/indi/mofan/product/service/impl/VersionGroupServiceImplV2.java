package indi.mofan.product.service.impl;

import indi.mofan.product.dubbo.service.IVersionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService(interfaceClass = IVersionGroupService.class, version = "2.0.0", group = "default", protocol = { "dubbo",
        "tri" })
public class VersionGroupServiceImplV2 implements IVersionGroupService {

    @Override
    public Map<String, Object> sayHello(String name) {
        log.info("Version 2.0.0 - sayHello called with name: {}", name);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello " + name + " from Version 2.0.0 - Enhanced");
        result.put("version", "2.0.0");
        result.put("group", "default");
        result.put("timestamp", System.currentTimeMillis());
        result.put("enhanced", true);

        return result;
    }

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "2.0.0");
        info.put("group", "default");
        info.put("description", "这是版本 2.0.0 的服务实现，增加了新功能");
        info.put("features", new String[] { "基础功能", "增强功能", "性能优化" });
        info.put("timestamp", System.currentTimeMillis());

        log.info("Version 2.0.0 - getServerInfo called");
        return info;
    }

    @Override
    public List<Map<String, Object>> getMenuItems() {
        log.info("Version 2.0.0 - getMenuItems called");

        List<Map<String, Object>> menuItems = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 5);
        item1.put("name", "数据分析");
        item1.put("path", "/analytics");
        item1.put("group", "default");
        menuItems.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 6);
        item2.put("name", "报表中心");
        item2.put("path", "/reports");
        item2.put("group", "default");
        menuItems.add(item2);

        return menuItems;
    }
}
