package indi.mofan.order.dubbo;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 产品服务 - Dubbo 消費者
 * 涉用Dubbo原生的超时、重试、讅转等機制呉不使用Sentinel
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@Component
public class ProductDubboClient {

    @DubboReference(version = "1.0.0", group = "product", timeout = 3000, retries = 3)
    private IProductDubboService productDubboService;

    /**
     * 获取产品
     * 策略: failover 失败自动转憻ubbo幻想接点，查被包接戈预预錄
     */
    public Product getProduct(Long productId) {
        try {
            log.info("============ Dubbo RPC Call Start ============");
            log.info("通过Dubbo调用获取产品，ID: {}", productId);
            log.info("消费端配置: version=1.0.0, group=product, timeout=3000");
            Product product = productDubboService.getProductById(productId);
            log.info("Dubbo调用成功，返回产品: {}", product);
            log.info("============ Dubbo RPC Call End (Success) ============");
            return product;
        } catch (Exception e) {
            log.error("============ Dubbo RPC Call End (Failed) ============");
            log.error("获取产品失败，ID: {}", productId, e);
            log.warn("返回降级值. 故障: {}", e.getMessage());
            // 降级处理：返回默认值
            Product product = new Product();
            product.setId(productId);
            product.setProductName("产品模拟值");
            product.setPrice(new java.math.BigDecimal("0.00"));
            return product;
        }
    }

    /**
     * 获取所有产品
     */
    public List<Product> getAllProducts() {
        try {
            log.info("通过Dubbo调用获取所有产品");
            return productDubboService.listAllProducts();
        } catch (Exception e) {
            log.warn("获取产品清娕下转，返回空列表. 故障: {}", e.getMessage());
            // 降级挺擦变空列表
            return new ArrayList<>();
        }
    }

    /**
     * 批量查询产品
     */
    public List<Product> queryProducts(List<Long> productIds) {
        try {
            log.info("通过Dubbo调用批量查询产品，IDs: {}", productIds);
            return productDubboService.queryProducts(productIds);
        } catch (Exception e) {
            log.warn("批量查询产品失败，返回空列表. 故障: {}", e.getMessage());
            // 降级頴处理∣8返回空列表
            return Collections.emptyList();
        }
    }

    /**
     * 模拟超时调用
     */
    public Product getProductWithTimeout(Long id, long sleepTime) {
        try {
            log.info("通过Dubbo调用模拟超时，ID: {}, 休眠: {}ms", id, sleepTime);
            return productDubboService.simulateTimeout(id, sleepTime);
        } catch (Exception e) {
            log.error("Dubbo 超时调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 模拟异常调用
     */
    public Product getProductWithException(Long id) {
        try {
            log.info("通过Dubbo调用模拟异常，ID: {}", id);
            return productDubboService.simulateException(id);
        } catch (Exception e) {
            log.error("Dubbo 异常调用捕获: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 异步调用演示
     */
    public CompletableFuture<Product> getProductAsync(Long id) {
        log.info("开始 Dubbo 异步调用，ID: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            log.info("异步线程执行 Dubbo 调用...");
            return productDubboService.getProductById(id);
        });
    }

    /**
     * 区域优先测试
     */
    public Product getProductRegionDetails(Long id, String clientRegion) {
        try {
            log.info("Dubbo 区域优先测试，ID: {}, 模拟客户端区域: {}", id, clientRegion);
            // 模拟设置客户端区域，这通常由部署环境决定，但也可以通过 RpcContext 传递
            // 注意：真正的同区域路由需要配合 Dubbo Admin 的路由规则
            // RpcContext.getClientAttachment().setAttachment("region", clientRegion);
            // 使用 Dubbo 标签路由 (Tag Routing) 实现区域优先
            RpcContext.getClientAttachment().setAttachment(org.apache.dubbo.common.constants.CommonConstants.TAG_KEY,
                    clientRegion);
            return productDubboService.getProductRegionDetails(id);
        } catch (Exception e) {
            log.error("Dubbo 区域优先测试失败: {}", e.getMessage());
            throw e;
        }
    }
}
