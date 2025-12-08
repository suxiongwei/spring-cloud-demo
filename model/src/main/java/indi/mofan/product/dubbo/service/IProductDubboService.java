package indi.mofan.product.dubbo.service;

import indi.mofan.product.bean.Product;

/**
 * 产品服务 - Dubbo RPC 接口
 * @author xiongweisu
 * @date 2025/3/23
 */
public interface IProductDubboService {

    /**
     * 根据ID获取产品
     * @param id 产品ID
     * @return 产品信息
     */
    Product getProductById(Long id);

    /**
     * 获取所有产品列表
     * @return 产品列表
     */
    java.util.List<Product> listAllProducts();

    /**
     * 批量查询产品
     * @param productIds 产品ID列表
     * @return 产品列表
     */
    java.util.List<Product> queryProducts(java.util.List<Long> productIds);
}
