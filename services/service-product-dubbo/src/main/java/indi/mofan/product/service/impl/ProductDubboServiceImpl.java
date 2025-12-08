package indi.mofan.product.service.impl;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品服务 Dubbo 实现
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@DubboService(version = "1.0.0", group = "product", timeout = 3000)
public class ProductDubboServiceImpl implements IProductDubboService {

    /**
     * 模拟产品数据库
     */
    private static final List<Product> PRODUCTS = Arrays.asList(
            new Product(1L, new BigDecimal("110"),"产品名", 10),
            new Product(2L, new BigDecimal("1110"),"产品名1", 11),
            new Product(3L, new BigDecimal("113210"),"产品名2", 12),
            new Product(4L, new BigDecimal("22"),"产品名3", 13),
            new Product(5L, new BigDecimal("323"),"产品名4", 14)
    );

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
}
