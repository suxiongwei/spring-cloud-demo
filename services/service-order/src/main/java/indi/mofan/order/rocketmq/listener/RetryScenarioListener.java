package indi.mofan.order.rocketmq.listener;

import indi.mofan.order.rocketmq.RocketMqDemoMessage;
import indi.mofan.order.rocketmq.RocketMqScenarioRuntimeState;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "${demo.rocketmq.topics.retry}",
        consumerGroup = "${demo.rocketmq.consumer-groups.retry}",
        selectorExpression = "ORDER_PAID",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING)
public class RetryScenarioListener implements RocketMQListener<MessageExt> {
    private final RocketMqMessageSupport messageSupport;
    private final RocketMqScenarioRuntimeState runtimeState;

    @Override
    public void onMessage(MessageExt messageExt) {
        RocketMqDemoMessage message = messageSupport.decode(messageExt);
        int attempt = messageExt.getReconsumeTimes() + 1;
        runtimeState.recordRetryAttempt(message.getMessageKey(), attempt);
        if (attempt <= 2) {
            throw new IllegalStateException("simulate transient failure");
        }
        runtimeState.markRetrySucceeded(message.getMessageKey());
    }
}
