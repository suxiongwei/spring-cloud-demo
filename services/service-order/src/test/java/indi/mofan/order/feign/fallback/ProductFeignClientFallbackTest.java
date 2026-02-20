package indi.mofan.order.feign.fallback;

import indi.mofan.product.bean.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductFeignClientFallbackTest {

    private final ProductFeignClientFallback fallback = new ProductFeignClientFallback();

    @Test
    void shouldReturnDefaultProductWhenFallbackTriggered() {
        Long productId = 1001L;

        Product product = fallback.getProductById(productId);

        assertThat(product.getId()).isEqualTo(productId);
        assertThat(product.getProductName()).isEqualTo("未知商品");
        assertThat(product.getNum()).isZero();
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("0"));
    }
}
