package indi.mofan.order.rocketmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@RocketMQTransactionListener
public class RocketMqDemoTransactionListener implements RocketMQLocalTransactionListener {
    private final RocketMqScenarioRuntimeState runtimeState;
    private final ObjectMapper objectMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        RocketMqDemoMessage payload = resolvePayload(message, arg);
        if (payload == null || payload.getTransactionId() == null) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        if ("rmq-08".equals(payload.getScenarioId())) {
            runtimeState.recordTxCheckState(payload.getTransactionId(), "UNKNOWN");
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        runtimeState.recordTxCheckState(payload.getTransactionId(), "COMMIT");
        runtimeState.markTxCheckCompleted(payload.getTransactionId());
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        RocketMqDemoMessage payload = resolvePayload(message, null);
        if (payload == null || payload.getTransactionId() == null) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        if ("rmq-08".equals(payload.getScenarioId())) {
            runtimeState.recordTxCheckState(payload.getTransactionId(), "ROLLBACK");
            runtimeState.markTxCheckCompleted(payload.getTransactionId());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        runtimeState.recordTxCheckState(payload.getTransactionId(), "COMMIT");
        runtimeState.markTxCheckCompleted(payload.getTransactionId());
        return RocketMQLocalTransactionState.COMMIT;
    }

    private RocketMqDemoMessage resolvePayload(Message message, Object arg) {
        if (arg instanceof RocketMqDemoMessage rocketMqDemoMessage) {
            return rocketMqDemoMessage;
        }
        Object payload = message.getPayload();
        if (payload instanceof String str) {
            return fromJson(str);
        }
        if (payload instanceof byte[] bytes) {
            return fromJson(new String(bytes, StandardCharsets.UTF_8));
        }
        return null;
    }

    private RocketMqDemoMessage fromJson(String json) {
        try {
            return objectMapper.readValue(json, RocketMqDemoMessage.class);
        } catch (Exception e) {
            return null;
        }
    }
}
