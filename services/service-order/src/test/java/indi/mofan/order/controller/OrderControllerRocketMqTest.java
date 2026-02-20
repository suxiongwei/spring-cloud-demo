package indi.mofan.order.controller;

import indi.mofan.common.ApiResponse;
import indi.mofan.order.facade.OrderBasicFacade;
import indi.mofan.order.facade.OrderDemoFacade;
import indi.mofan.order.facade.RocketMqDemoFacade;
import indi.mofan.order.service.CommonResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerRocketMqTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderBasicFacade orderBasicFacade;

    @MockBean
    private OrderDemoFacade orderDemoFacade;

    @MockBean
    private RocketMqDemoFacade rocketMqDemoFacade;

    @MockBean
    private CommonResourceService commonResourceService;

    @BeforeEach
    void setUp() {
        when(rocketMqDemoFacade.publishBasicDemo()).thenReturn(scenario("rmq-01"));
        when(rocketMqDemoFacade.retryDemo()).thenReturn(scenario("rmq-02"));
        when(rocketMqDemoFacade.dlqDemo()).thenReturn(scenario("rmq-03"));
        when(rocketMqDemoFacade.idempotentDemo()).thenReturn(scenario("rmq-04"));
        when(rocketMqDemoFacade.orderlyDemo()).thenReturn(scenario("rmq-05"));
        when(rocketMqDemoFacade.delayCloseDemo()).thenReturn(scenario("rmq-06"));
        when(rocketMqDemoFacade.txSendDemo()).thenReturn(scenario("rmq-07"));
        when(rocketMqDemoFacade.txCheckDemo()).thenReturn(scenario("rmq-08"));
        when(rocketMqDemoFacade.tagFilterDemo()).thenReturn(scenario("rmq-09"));
        when(rocketMqDemoFacade.replayDlqDemo()).thenReturn(scenario("rmq-10"));
    }

    @Test
    void shouldExposePublishBasicEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/publish-basic", "rmq-01");
    }

    @Test
    void shouldExposeRetryEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/retry", "rmq-02");
    }

    @Test
    void shouldExposeDlqEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/dlq", "rmq-03");
    }

    @Test
    void shouldExposeIdempotentEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/idempotent", "rmq-04");
    }

    @Test
    void shouldExposeOrderlyEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/orderly", "rmq-05");
    }

    @Test
    void shouldExposeDelayCloseEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/delay-close", "rmq-06");
    }

    @Test
    void shouldExposeTxSendEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/tx/send", "rmq-07");
    }

    @Test
    void shouldExposeTxCheckEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/tx/check", "rmq-08");
    }

    @Test
    void shouldExposeTagFilterEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/tag-filter", "rmq-09");
    }

    @Test
    void shouldExposeReplayDlqEndpoint() throws Exception {
        assertScenario("/demo/rocketmq/replay-dlq", "rmq-10");
    }

    private void assertScenario(String endpoint, String scenarioId) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.scenarioId").value(scenarioId));
    }

    private static ApiResponse<Object> scenario(String scenarioId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("scenarioId", scenarioId);
        payload.put("principle", "test");
        return ApiResponse.success("ok", payload);
    }
}
