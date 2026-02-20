package indi.mofan.order.controller;

import indi.mofan.common.ApiResponse;
import indi.mofan.order.facade.OrderDemoFacade;
import indi.mofan.order.feign.ProductFeignClient;
import indi.mofan.order.properties.CkProperties;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CoreScenarioEvidenceContractTest {

    @Test
    void gatewayRoutingScenarioShouldContainEvidenceFields() {
        OrderDemoFacade facade = new OrderDemoFacade(
                mock(DiscoveryClient.class),
                mock(ProductFeignClient.class),
                mock(CkProperties.class),
                mock(Environment.class)
        );

        ApiResponse<Object> response = facade.gatewayRoutingDemo();
        assertThat(response.getCode()).isEqualTo(200);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertThat(data.get("scenarioId")).isEqualTo("gateway-routing");
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("failureInjected")).isEqualTo(false);
        assertThat(data.get("costMs")).isInstanceOf(Number.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> evidence = (Map<String, Object>) data.get("evidence");
        assertThat(evidence.get("routeCount")).isEqualTo(4);
        assertThat(data.get("nextQuestionHints")).isInstanceOf(Iterable.class);
    }
}
