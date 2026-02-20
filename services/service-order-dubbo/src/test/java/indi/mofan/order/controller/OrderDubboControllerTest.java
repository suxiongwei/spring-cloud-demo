package indi.mofan.order.controller;

import indi.mofan.order.dubbo.ProductDubboClient;
import indi.mofan.product.bean.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderDubboController.class)
class OrderDubboControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductDubboClient productDubboClient;

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("service-order-dubbo 健康状态正常"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturnDubboSyncResult() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setProductName("p1");
        product.setPrice(new BigDecimal("9.9"));
        when(productDubboClient.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/call-sync").param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.product.id").value(1))
                .andExpect(jsonPath("$.data.product.productName").value("p1"))
                .andExpect(jsonPath("$.data.scenarioId").value("dubbo-call-sync"))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.failureInjected").value(false))
                .andExpect(jsonPath("$.data.costMs").isNumber())
                .andExpect(jsonPath("$.data.evidence.productId").value(1));
    }
}
