package indi.mofan.product.controller;

import indi.mofan.product.bean.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import indi.mofan.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiongweisu
 * @date 2025/3/23 17:24
 */
@RestController
@Tag(name = "产品接口")
// @RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @GetMapping("/product/{id}")
    @Operation(summary = "根据ID查询产品")
    @SneakyThrows
    public Product getProduct(@PathVariable("id") Long id,
            HttpServletRequest request) {
        String header = request.getHeader("X-Token");
        System.out.println("调用了getProduct方法, XToken: " + header + ", id: " + id);
        Product product = productService.getProductById(id);
        String port = environment.getProperty("server.port");
        product.setPort(port);

        /*
         * 模拟慢调用
         */
        if (id == 88888) {
            TimeUnit.SECONDS.sleep(5);
        }

        /*
         * 模拟异常
         */
        if (id == 99999) {
            throw new IllegalArgumentException("ID不能为99999");
        }

        return product;
    }
}
