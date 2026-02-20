package indi.mofan.order.rocketmq.listener;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "${demo.rocketmq.topics.dlq-trigger}",
        consumerGroup = "${demo.rocketmq.consumer-groups.dlq-trigger}",
        selectorExpression = "ORDER_STOCK_RESERVE",
        maxReconsumeTimes = 2,
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING)
public class DlqTriggerListener implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt messageExt) {
        throw new IllegalStateException("simulate poison message to trigger DLQ");
    }
}
