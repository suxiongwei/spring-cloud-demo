package indi.mofan.order.controller;

import indi.mofan.order.common.ApiResponse;
import indi.mofan.order.common.ResultCode;
import indi.mofan.order.dubbo.ProductDubboClient;
import indi.mofan.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务 - Dubbo 版本 Controller
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@RestController
public class OrderDubboController {

    @Autowired
    private ProductDubboClient productDubboClient;

    /**
     * Dubbo 同步调用演示
     */
    @GetMapping("/call-sync")
    public ApiResponse<Map<String, Object>> dubboCallSync(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            long startTime = System.currentTimeMillis();
            Product product = productDubboClient.getProduct(productId);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 同步调用");

            return ApiResponse.success("Dubbo 同步调用成功", result);
        } catch (Exception e) {
            log.error("Dubbo 同步调用失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 调用失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 批量查询演示
     */
    @GetMapping("/call-batch")
    public ApiResponse<Map<String, Object>> dubboCallBatch(
            @RequestParam(value = "productIds", defaultValue = "1,2,3") String productIds) {
        try {
            long startTime = System.currentTimeMillis();
            List<Long> ids = Arrays.stream(productIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            List<Product> products = productDubboClient.queryProducts(ids);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("count", products.size());
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 批量查询");

            return ApiResponse.success("Dubbo 批量查询成功", result);
        } catch (Exception e) {
            log.error("Dubbo 批量查询失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 批量查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有产品
     */
    @GetMapping("/list-all")
    public ApiResponse<Map<String, Object>> dubboListAll() {
        try {
            long startTime = System.currentTimeMillis();
            List<Product> products = productDubboClient.getAllProducts();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("count", products.size());
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 查询列表");

            return ApiResponse.success("Dubbo 查询成功", result);
        } catch (Exception e) {
            log.error("Dubbo 查询失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("service-order-dubbo 健康状态正常");
    }
}
