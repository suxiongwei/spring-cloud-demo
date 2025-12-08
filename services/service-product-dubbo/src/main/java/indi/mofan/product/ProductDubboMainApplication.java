package indi.mofan.product;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 产品服务 - Dubbo 版本
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
@EnableDubbo
@SpringBootApplication
public class ProductDubboMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductDubboMainApplication.class, args);
    }
}
