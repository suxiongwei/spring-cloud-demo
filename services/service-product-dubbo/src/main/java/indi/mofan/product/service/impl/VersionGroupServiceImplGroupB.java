package indi.mofan.product.service.impl;

import indi.mofan.product.dubbo.service.IVersionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService(interfaceClass = IVersionGroupService.class, version = "1.0.0", group = "groupB", protocol = { "dubbo",
        "tri" })
public class VersionGroupServiceImplGroupB implements IVersionGroupService {

    @Override
    public Map<String, Object> sayHello(String name) {
        log.info("Group B Version 1.0.0 - sayHello called with name: {}", name);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello " + name + " from Group B Version 1.0.0");
        result.put("version", "1.0.0");
        result.put("group", "groupB");
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("group", "groupB");
        info.put("description", "这是分组 B 版本 1.0.0 的服务实现");
        info.put("features", new String[] { "分组B专用功能", "测试环境" });
        info.put("timestamp", System.currentTimeMillis());

        log.info("Group B Version 1.0.0 - getServerInfo called");
        return info;
    }

    @Override
    public List<Map<String, Object>> getMenuItems() {
        log.info("Group B Version 1.0.0 - getMenuItems called");

        List<Map<String, Object>> menuItems = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 3);
        item1.put("name", "库存管理");
        item1.put("path", "/inventory");
        item1.put("group", "groupB");
        menuItems.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 4);
        item2.put("name", "供应商管理");
        item2.put("path", "/suppliers");
        item2.put("group", "groupB");
        menuItems.add(item2);

        return menuItems;
    }
}
