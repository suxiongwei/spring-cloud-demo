package indi.mofan.order.rocketmq;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RocketMqDemoMessage {
    private String scenarioId;
    private String messageKey;
    private String orderId;
    private String event;
    private String transactionId;
    private String replaySourceKey;
    private String tag;
    private Map<String, String> attributes = new HashMap<>();
}
