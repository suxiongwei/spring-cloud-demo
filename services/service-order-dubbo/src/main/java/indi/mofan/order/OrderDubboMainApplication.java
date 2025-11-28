package indi.mofan.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单服务 - Dubbo 版本
 * 
 * @author mofan
 * @date 2025/3/23
 */
@EnableDubbo
@SpringBootApplication
public class OrderDubboMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderDubboMainApplication.class, args);
    }
}
