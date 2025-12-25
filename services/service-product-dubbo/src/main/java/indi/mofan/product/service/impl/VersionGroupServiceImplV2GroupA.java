package indi.mofan.product.service.impl;

import indi.mofan.product.dubbo.service.IVersionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@DubboService(interfaceClass = IVersionGroupService.class, version = "2.0.0", group = "groupA")
public class VersionGroupServiceImplV2GroupA implements IVersionGroupService {

    @Override
    public Map<String, Object> sayHello(String name) {
        log.info("Group A Version 2.0.0 - sayHello called with name: {}", name);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello " + name + " from Group A Version 2.0.0 - Premium");
        result.put("version", "2.0.0");
        result.put("group", "groupA");
        result.put("timestamp", System.currentTimeMillis());
        result.put("premium", true);
        
        return result;
    }

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "2.0.0");
        info.put("group", "groupA");
        info.put("description", "这是分组 A 版本 2.0.0 的服务实现，高级功能");
        info.put("features", new String[]{"分组A专用功能", "高级功能", "性能优化", "企业级"});
        info.put("timestamp", System.currentTimeMillis());
        
        log.info("Group A Version 2.0.0 - getServerInfo called");
        return info;
    }
}
