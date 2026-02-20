package indi.mofan.order.rocketmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "demo.rocketmq")
public class RocketMqDemoProperties {
    private boolean strictRealMode = true;
    private long awaitTimeoutMs = 15000;
    private Topics topics = new Topics();
    private ConsumerGroups consumerGroups = new ConsumerGroups();

    @Data
    public static class Topics {
        private String orderEvents;
        private String retry;
        private String dlqTrigger;
        private String idempotent;
        private String orderly;
        private String delayClose;
        private String tx;
        private String tag;
        private String dlqObserve;
    }

    @Data
    public static class ConsumerGroups {
        private String basicInventory;
        private String basicMarketing;
        private String retry;
        private String dlqTrigger;
        private String dlqObserver;
        private String idempotent;
        private String orderly;
        private String delayClose;
        private String tagInventory;
        private String tagMarketing;
        private String txConsumer;
    }
}
