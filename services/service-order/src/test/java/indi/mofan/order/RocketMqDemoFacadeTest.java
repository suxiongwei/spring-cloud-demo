package indi.mofan.order;

import indi.mofan.common.ApiResponse;
import indi.mofan.order.facade.RocketMqDemoFacade;
import indi.mofan.order.rocketmq.RocketMqDemoMessage;
import indi.mofan.order.rocketmq.RocketMqDemoProducer;
import indi.mofan.order.rocketmq.RocketMqScenarioRuntimeState;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RocketMqDemoFacadeTest {

    @Test
    void shouldExposeEvidenceFieldsForPublishBasicSuccess() {
        RocketMqDemoProducer producer = mock(RocketMqDemoProducer.class);
        RocketMqScenarioRuntimeState state = new RocketMqScenarioRuntimeState();
        doAnswer(invocation -> {
            RocketMqDemoMessage message = invocation.getArgument(0);
            state.recordBasicDelivery(message.getMessageKey(), "inventory-group");
            state.recordBasicDelivery(message.getMessageKey(), "marketing-group");
            return null;
        }).when(producer).sendNormal(any());

        RocketMqDemoFacade facade = new RocketMqDemoFacade(producer, state);
        ApiResponse<Object> response = facade.publishBasicDemo();

        assertThat(response.getCode()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertThat(data).containsEntry("success", true);
        assertThat(data).containsEntry("failureInjected", false);
        assertThat(data).containsKeys("costMs", "evidence", "nextQuestionHints");
    }

    @Test
    void shouldFailWhenBrokerUnavailableForPublishBasicScenario() {
        RocketMqDemoProducer producer = mock(RocketMqDemoProducer.class);
        RocketMqScenarioRuntimeState state = new RocketMqScenarioRuntimeState();
        doThrow(new RuntimeException("broker unavailable")).when(producer).sendNormal(any());

        RocketMqDemoFacade facade = new RocketMqDemoFacade(producer, state);

        ApiResponse<Object> response = facade.publishBasicDemo();
        assertThat(response.getCode()).isEqualTo(500);
        assertThat(response.getMsg()).contains("broker unavailable");
    }

    @Test
    void shouldFailWhenBrokerUnavailableForTransactionSendScenario() {
        RocketMqDemoProducer producer = mock(RocketMqDemoProducer.class);
        RocketMqScenarioRuntimeState state = new RocketMqScenarioRuntimeState();
        when(producer.sendTransaction(any())).thenThrow(new RuntimeException("connect failure"));

        RocketMqDemoFacade facade = new RocketMqDemoFacade(producer, state);

        ApiResponse<Object> response = facade.txSendDemo();
        assertThat(response.getCode()).isEqualTo(500);
        assertThat(response.getMsg()).contains("connect failure");
    }
}
