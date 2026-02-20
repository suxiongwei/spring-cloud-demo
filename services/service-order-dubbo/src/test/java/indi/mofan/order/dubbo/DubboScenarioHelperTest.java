package indi.mofan.order.dubbo;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DubboScenarioHelperTest {
    private final DubboScenarioHelper helper = new DubboScenarioHelper();

    @Test
    void shouldBuildLoadBalanceResult() {
        Product product = new Product();
        product.setId(1L);
        product.setProductName("server-a");
        product.setPrice(new BigDecimal("10"));

        Map<String, Object> result = helper.testLoadBalanceStrategy(
                "random",
                "desc",
                3,
                () -> product);

        assertThat(result.get("strategy")).isEqualTo("random");
        assertThat(result.get("requestCount")).isEqualTo(3);
        assertThat((Map<String, Integer>) result.get("serverDistribution"))
                .containsEntry("server-a", 3);
    }

    @Test
    void shouldCompareProtocolsAndGenerateSummary() {
        IProductDubboService dubbo = mock(IProductDubboService.class);
        IProductDubboService triple = mock(IProductDubboService.class);
        IProductDubboService rest = mock(IProductDubboService.class);

        Product product = new Product();
        product.setId(1L);
        product.setProductName("ok");
        when(dubbo.getProductById(1L)).thenReturn(product);
        when(triple.getProductById(1L)).thenReturn(product);
        when(rest.getProductById(1L)).thenReturn(product);

        Map<String, Object> result = helper.compareProtocols(dubbo, triple, rest, 1L, 2);

        assertThat(result).containsKeys("dubbo", "triple", "rest", "summary");
        Map<String, Object> dubboResult = (Map<String, Object>) result.get("dubbo");
        assertThat(dubboResult.get("successCount")).isEqualTo(2);
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        assertThat(summary).containsKeys("mostReliableProtocol", "highestSuccessCount");
    }
}
