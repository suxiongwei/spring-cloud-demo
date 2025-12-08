package indi.mofan.order.service;


import indi.mofan.order.bean.Order;

/**
 * @author xiongweisu
 * @date 2025/3/23 17:36
 */
public interface OrderService {
    Order createOrder(Long productId, Long userId);
}
