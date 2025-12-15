package indi.mofan.product.service.impl;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品服务 Dubbo 实现
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@DubboService(version = "1.0.0", group = "product", timeout = 2000, retries = 2)
public class ProductDubboServiceImpl implements IProductDubboService {

    /**
     * 模拟产品数据库
     */
    private static final List<Product> PRODUCTS = Arrays.asList(
            new Product(1L, new BigDecimal("110"), "产品名", 10),
            new Product(2L, new BigDecimal("1110"), "产品名1", 11),
            new Product(3L, new BigDecimal("113210"), "产品名2", 12),
            new Product(4L, new BigDecimal("22"), "产品名3", 13),
            new Product(5L, new BigDecimal("323"), "产品名4", 14));

    @Override
    public Product getProductById(Long id) {
        log.info("Dubbo Service: 查询产品，ID: {}", id);
        return PRODUCTS.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> listAllProducts() {
        log.info("Dubbo Service: 获取所有产品");
        return new java.util.ArrayList<>(PRODUCTS);
    }

    @Override
    public List<Product> queryProducts(List<Long> productIds) {
        log.info("Dubbo Service: 批量查询产品，IDs: {}", productIds);
        return PRODUCTS.stream()
                .filter(p -> productIds.contains(p.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Product simulateTimeout(Long id, long sleepTime) {
        log.info("Dubbo Service: 模拟超时调用，ID: {}, 休眠: {}ms", id, sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return getProductById(id);
    }

    @Override
    public Product simulateException(Long id) {
        log.info("Dubbo Service: 模拟异常调用，ID: {}", id);
        throw new RuntimeException("Dubbo 服务端模拟异常: 数据库连接失败");
    }

    @Value("${dubbo.registry.parameters.region:unknown}")
    private String region;

    @Override
    public Product getProductRegionDetails(Long id) {
        // 获取当前服务提供者的区域配置
        // String region =
        // RpcContext.getServiceContext().getUrl().getParameter("region", "unknown");
        log.info("Dubbo Service: 区域优先测试，ID: {}, 当前区域: {}", id, region);

        Product product = getProductById(id);
        if (product != null) {
            // 克隆对象以免修改原对象（虽然这里是模拟数据）
            Product p = new Product(product.getId(), product.getPrice(), product.getProductName(), product.getNum());
            p.setProductName(p.getProductName() + " [Region: " + region + "]");
            return p;
        }
        return null;
    }
}
