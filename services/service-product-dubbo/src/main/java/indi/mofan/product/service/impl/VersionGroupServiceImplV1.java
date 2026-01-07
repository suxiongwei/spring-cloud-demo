package indi.mofan.product.service.impl;

import indi.mofan.product.dubbo.service.IVersionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService(interfaceClass = IVersionGroupService.class, version = "1.0.0", group = "default", protocol = { "dubbo",
        "tri" })
public class VersionGroupServiceImplV1 implements IVersionGroupService {

    @Override
    public Map<String, Object> sayHello(String name) {
        log.info("Version 1.0.0 - sayHello called with name: {}", name);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello " + name + " from Version 1.0.0");
        result.put("version", "1.0.0");
        result.put("group", "default");
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("group", "default");
        info.put("description", "这是版本 1.0.0 的服务实现");
        info.put("features", new String[] { "基础功能", "稳定版本" });
        info.put("timestamp", System.currentTimeMillis());

        log.info("Version 1.0.0 - getServerInfo called");
        return info;
    }

    @Override
    public List<Map<String, Object>> getMenuItems() {
        log.info("Version 1.0.0 - getMenuItems called");

        List<Map<String, Object>> menuItems = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("name", "首页");
        item1.put("path", "/home");
        item1.put("group", "default");
        menuItems.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("name", "用户管理");
        item2.put("path", "/users");
        item2.put("group", "default");
        menuItems.add(item2);

        return menuItems;
    }
}
