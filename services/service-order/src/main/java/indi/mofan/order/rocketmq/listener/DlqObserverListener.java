package indi.mofan.order.rocketmq.listener;

import indi.mofan.order.rocketmq.RocketMqDemoMessage;
import indi.mofan.order.rocketmq.RocketMqDemoProperties;
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
@RocketMQMessageListener(topic = "${demo.rocketmq.topics.dlq-observe}",
        consumerGroup = "${demo.rocketmq.consumer-groups.dlq-observer}",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING)
public class DlqObserverListener implements RocketMQListener<MessageExt> {
    private final RocketMqMessageSupport messageSupport;
    private final RocketMqScenarioRuntimeState runtimeState;
    private final RocketMqDemoProperties properties;

    @Override
    public void onMessage(MessageExt messageExt) {
        RocketMqDemoMessage message = messageSupport.decode(messageExt);
        runtimeState.recordDlqObserved(message.getMessageKey(), properties.getTopics().getDlqObserve(),
                properties.getTopics().getDlqTrigger(), "max-reconsume-exceeded");
    }
}
