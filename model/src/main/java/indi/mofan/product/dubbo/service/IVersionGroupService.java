package indi.mofan.product.dubbo.service;

import java.util.Map;

public interface IVersionGroupService {

    Map<String, Object> sayHello(String name);

    Map<String, Object> getServerInfo();
}
