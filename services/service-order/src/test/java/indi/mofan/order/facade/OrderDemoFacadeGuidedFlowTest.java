package indi.mofan.order.facade;

import indi.mofan.common.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OrderDemoFacadeGuidedFlowTest {

    @Test
    void shouldExposeExecutableGuidedFlowV2Contract() {
        OrderDemoFacade facade = new OrderDemoFacade(
                mock(DiscoveryClient.class),
                mock(indi.mofan.order.feign.ProductFeignClient.class),
                mock(indi.mofan.order.properties.CkProperties.class),
                mock(Environment.class)
        );

        ApiResponse<Object> response = facade.guidedInterviewFlow("http://localhost:9090");
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(200);

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getData();
        assertThat(payload.get("flowName")).isEqualTo("java-senior-interview-guided-flow-v2");
        assertThat(payload.get("runId")).isInstanceOf(String.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) payload.get("steps");
        assertThat(steps).isNotEmpty();
        assertThat(steps.get(0)).containsKey("assertion");

        @SuppressWarnings("unchecked")
        Map<String, Object> assertion = (Map<String, Object>) steps.get(0).get("assertion");
        assertThat(assertion).containsKeys("rule", "expected");
    }
}
