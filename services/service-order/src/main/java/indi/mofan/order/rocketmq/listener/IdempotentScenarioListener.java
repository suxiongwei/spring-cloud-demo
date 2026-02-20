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
@RocketMQMessageListener(topic = "${demo.rocketmq.topics.idempotent}",
        consumerGroup = "${demo.rocketmq.consumer-groups.idempotent}",
        selectorExpression = "ORDER_INVENTORY",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING)
public class IdempotentScenarioListener implements RocketMQListener<MessageExt> {
    private final RocketMqMessageSupport messageSupport;
    private final RocketMqScenarioRuntimeState runtimeState;

    @Override
    public void onMessage(MessageExt messageExt) {
        RocketMqDemoMessage message = messageSupport.decode(messageExt);
        runtimeState.recordIdempotentReceive(message.getMessageKey());
        runtimeState.markIdempotentProcessIfAbsent(message.getMessageKey());
    }
}
