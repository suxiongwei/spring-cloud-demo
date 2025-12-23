package indi.mofan.product.dubbo.service;

import indi.mofan.product.bean.Product;

import java.util.List;
import java.util.Map;

/**
 * 产品服务 - Dubbo RPC 接口
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
public interface IProductDubboService {

    /**
     * 根据ID获取产品
     * 
     * @param id 产品ID
     * @return 产品信息
     */
    Product getProductById(Long id);

    /**
     * 获取所有产品列表
     * 
     * @return 产品列表
     */
    List<Product> listAllProducts();

    /**
     * 批量查询产品
     * 
     * @param productIds 产品ID列表
     * @return 产品列表
     */
    List<Product> queryProducts(java.util.List<Long> productIds);

    /**
     * 模拟超时调用
     * 
     * @param id        产品ID
     * @param sleepTime 睡眠时间(ms)
     * @return 产品信息
     */
    Product simulateTimeout(Long id, long sleepTime);

    /**
     * 模拟异常调用
     * 
     * @param id 产品ID
     * @return 产品信息
     */
    Product simulateException(Long id);

    /**
     * 获取产品及区域信息
     *
     * @param id 产品ID
     * @return 产品信息(包含区域详情)
     */
    Product getProductRegionDetails(Long id);

    /**
     * 服务端并发控制测试
     * 
     * @param concurrentCount 并发数量
     * @param sleepTime 休眠时间(ms)
     * @return 测试结果
     */
    Map<String, Object> testConcurrencyControl(Long sleepTime);

    /**
     * 消费端并发控制测试
     * 
     * @return 测试结果
     */
    Map<String, Object> testActivesControl();

    /**
     * 最小并发数负载均衡测试
     * 
     * @return 测试结果
     */
    Map<String, Object> testLeastActiveLoadBalance();
}
