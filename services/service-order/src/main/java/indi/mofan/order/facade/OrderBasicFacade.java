package indi.mofan.order.facade;

import indi.mofan.common.ApiResponse;
import indi.mofan.order.bean.Order;
import indi.mofan.order.properties.OrderProperties;
import indi.mofan.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderBasicFacade {
    private final OrderService orderService;
    private final OrderProperties orderProperties;

    public ApiResponse<String> config() {
        String msg = "order timeout: " + orderProperties.getTimeout()
                + " auto-confirm: " + orderProperties.getAutoConfirm()
                + " db-url: " + orderProperties.getDbUrl();
        return ApiResponse.success(msg);
    }

    public ApiResponse<Order> createOrder(Long userId, Long productId) {
        Order order = orderService.createOrder(productId, userId);
        return ApiResponse.success("订单创建成功", order);
    }
}
