package indi.mofan.order.rocketmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RocketMqDemoProducer {
    public static final int DELAY_LEVEL_10_SECONDS = 3;

    private final RocketMQTemplate rocketMQTemplate;
    private final RocketMqDemoProperties properties;
    private final ObjectMapper objectMapper;

    public void sendNormal(RocketMqDemoMessage message) {
        sendSync(buildDestination(properties.getTopics().getOrderEvents(), message.getTag()), message);
    }

    public void sendRetry(RocketMqDemoMessage message) {
        sendSync(buildDestination(properties.getTopics().getRetry(), message.getTag()), message);
    }

    public void sendDlqTrigger(RocketMqDemoMessage message) {
        sendSync(buildDestination(properties.getTopics().getDlqTrigger(), message.getTag()), message);
    }

    public void sendIdempotent(RocketMqDemoMessage message) {
        sendSync(buildDestination(properties.getTopics().getIdempotent(), message.getTag()), message);
    }

    public void sendOrderly(RocketMqDemoMessage message, String hashKey) {
        rocketMQTemplate.syncSendOrderly(buildDestination(properties.getTopics().getOrderly(), message.getTag()),
                buildMessage(message), hashKey, 5000);
    }

    public void sendDelayClose(RocketMqDemoMessage message) {
        rocketMQTemplate.syncSend(buildDestination(properties.getTopics().getDelayClose(), message.getTag()),
                buildMessage(message), 5000, DELAY_LEVEL_10_SECONDS);
    }

    public TransactionSendResult sendTransaction(RocketMqDemoMessage message) {
        return rocketMQTemplate.sendMessageInTransaction(
                buildDestination(properties.getTopics().getTx(), message.getTag()),
                buildTransactionMessage(properties.getTopics().getTx(), message.getTag(), message), message);
    }

    public void sendTag(RocketMqDemoMessage message) {
        sendSync(buildDestination(properties.getTopics().getTag(), message.getTag()), message);
    }

    public void sendReplay(RocketMqDemoMessage message) {
        sendSync(buildDestination(properties.getTopics().getOrderEvents(), message.getTag()), message);
    }

    private void sendSync(String destination, RocketMqDemoMessage message) {
        rocketMQTemplate.syncSend(destination, buildMessage(message), 5000);
    }

    private Message<String> buildMessage(RocketMqDemoMessage payload) {
        String body = toJson(payload);
        return MessageBuilder.withPayload(body)
                .setHeader(RocketMQHeaders.KEYS, payload.getMessageKey())
                .build();
    }

    private Message<String> buildTransactionMessage(String topic, String tag, RocketMqDemoMessage payload) {
        String body = toJson(payload);
        return MessageBuilder.withPayload(body)
                .setHeader(RocketMQHeaders.KEYS, payload.getMessageKey())
                .setHeader(RocketMQHeaders.TOPIC, topic)
                .setHeader(RocketMQHeaders.TAGS, tag)
                .build();
    }

    private String buildDestination(String topic, String tag) {
        return tag == null || tag.isBlank() ? topic : topic + ":" + tag;
    }

    private String toJson(RocketMqDemoMessage payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to serialize message", e);
        }
    }
}
