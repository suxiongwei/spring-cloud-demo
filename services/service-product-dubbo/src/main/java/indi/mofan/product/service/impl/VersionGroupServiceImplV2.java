package indi.mofan.product.service.impl;

import indi.mofan.product.dubbo.service.IVersionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@DubboService(interfaceClass = IVersionGroupService.class, version = "2.0.0", group = "default")
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
        info.put("features", new String[]{"基础功能", "增强功能", "性能优化"});
        info.put("timestamp", System.currentTimeMillis());
        
        log.info("Version 2.0.0 - getServerInfo called");
        return info;
    }
}
