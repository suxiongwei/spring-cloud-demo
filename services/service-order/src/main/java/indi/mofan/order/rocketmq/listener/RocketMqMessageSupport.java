package indi.mofan.order.rocketmq.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import indi.mofan.order.rocketmq.RocketMqDemoMessage;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RocketMqMessageSupport {
    private final ObjectMapper objectMapper;

    public RocketMqDemoMessage decode(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            return objectMapper.readValue(body, RocketMqDemoMessage.class);
        } catch (Exception e) {
            throw new IllegalStateException("failed to decode rocketmq message", e);
        }
    }
}
