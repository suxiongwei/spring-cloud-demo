package indi.mofan.order.facade;

import indi.mofan.common.ApiResponse;
import indi.mofan.common.ResultCode;
import indi.mofan.order.rocketmq.RocketMqDemoMessage;
import indi.mofan.order.rocketmq.RocketMqDemoProducer;
import indi.mofan.order.rocketmq.RocketMqDemoProperties;
import indi.mofan.order.rocketmq.RocketMqScenarioRuntimeState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Service
public class RocketMqDemoFacade {

    private static final String SCENARIO_BASIC = "rmq-01";
    private static final String SCENARIO_RETRY = "rmq-02";
    private static final String SCENARIO_DLQ = "rmq-03";
    private static final String SCENARIO_IDEMPOTENT = "rmq-04";
    private static final String SCENARIO_ORDERLY = "rmq-05";
    private static final String SCENARIO_DELAY = "rmq-06";
    private static final String SCENARIO_TX_SEND = "rmq-07";
    private static final String SCENARIO_TX_CHECK = "rmq-08";
    private static final String SCENARIO_TAG = "rmq-09";
    private static final String SCENARIO_REPLAY = "rmq-10";

    private final RocketMqDemoProducer producer;
    private final RocketMqScenarioRuntimeState runtimeState;
    private final RocketMqDemoProperties properties;
    private final AtomicLong sequence = new AtomicLong(1000);

    @Autowired
    public RocketMqDemoFacade(RocketMqDemoProducer producer, RocketMqScenarioRuntimeState runtimeState,
            RocketMqDemoProperties properties) {
        this.producer = producer;
        this.runtimeState = runtimeState;
        this.properties = properties;
    }

    public RocketMqDemoFacade(RocketMqDemoProducer producer, RocketMqScenarioRuntimeState runtimeState) {
        this(producer, runtimeState, defaultProperties());
    }

    public ApiResponse<Object> publishBasicDemo() {
        return executeScenario("RocketMQ 普通消息场景失败", () -> {
            String orderId = nextOrderId();
            String messageKey = nextMessageKey("BASIC");

            RocketMqDemoMessage message = newMessage(SCENARIO_BASIC, messageKey, orderId, "ORDER_CREATED", null,
                    "ORDER_CREATED", null);
            producer.sendNormal(message);
            assertOrThrow(runtimeState.awaitBasicDelivery(messageKey, 2, timeout()),
                    "等待库存/营销消费者完成超时");

            Map<String, Object> payload = basePayload(SCENARIO_BASIC, "订单创建后异步通知库存与营销");
            payload.put("topic", topics().getOrderEvents());
            payload.put("tag", "ORDER_CREATED");
            payload.put("messageKey", messageKey);
            payload.put("orderId", orderId);
            payload.put("producerGroup", "order-demo-producer-group");
            payload.put("consumerGroups", List.of(groups().getBasicInventory(), groups().getBasicMarketing()));
            payload.put("deliveryResult", runtimeState.getBasicDeliveries(messageKey));
            payload.put("principle", "普通消息用于异步解耦与削峰，生产者和消费者独立扩展。");
            return payload;
        }, "RocketMQ 普通消息场景完成");
    }

    public ApiResponse<Object> retryDemo() {
        return executeScenario("RocketMQ 重试场景失败", () -> {
            String messageKey = nextMessageKey("RETRY");
            RocketMqDemoMessage message = newMessage(SCENARIO_RETRY, messageKey, null, "ORDER_PAID", null,
                    "ORDER_PAID", null);

            producer.sendRetry(message);
            assertOrThrow(runtimeState.awaitRetrySucceeded(messageKey, timeout()), "等待重试成功超时");

            List<Integer> attempts = runtimeState.getRetryAttempts(messageKey);
            List<Map<String, Object>> attemptDetails = attempts.stream().map(attempt -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("attempt", attempt);
                item.put("status", attempt >= 3 ? "SUCCESS" : "FAILED");
                return item;
            }).toList();

            Map<String, Object> payload = basePayload(SCENARIO_RETRY, "消费失败自动重试");
            payload.put("topic", topics().getRetry());
            payload.put("tag", "ORDER_PAID");
            payload.put("messageKey", messageKey);
            payload.put("attempts", attemptDetails);
            payload.put("finalStatus", "SUCCESS");
            payload.put("principle", "RocketMQ 默认至少一次投递，消费端需兼容重复与重试。");
            return payload;
        }, "RocketMQ 重试场景完成");
    }

    public ApiResponse<Object> dlqDemo() {
        return executeScenario("RocketMQ 死信场景失败", () -> {
            String messageKey = nextMessageKey("DLQ");
            RocketMqDemoMessage message = newMessage(SCENARIO_DLQ, messageKey, null, "ORDER_STOCK_RESERVE", null,
                    "ORDER_STOCK_RESERVE", null);

            producer.sendDlqTrigger(message);
            assertOrThrow(runtimeState.awaitDlqObserved(messageKey, timeout()), "等待死信观察超时");

            Map<String, Object> deadLetter = runtimeState.findDlqMessage(messageKey).orElseGet(() -> {
                Map<String, Object> fallback = new LinkedHashMap<>();
                fallback.put("messageKey", messageKey);
                fallback.put("topic", topics().getDlqObserve());
                fallback.put("originTopic", topics().getDlqTrigger());
                fallback.put("reason", "max-reconsume-exceeded");
                return fallback;
            });

            Map<String, Object> payload = basePayload(SCENARIO_DLQ, "重试耗尽后转入死信队列");
            payload.put("topic", topics().getDlqTrigger());
            payload.put("tag", "ORDER_STOCK_RESERVE");
            payload.put("messageKey", messageKey);
            payload.put("deadLetter", deadLetter);
            payload.put("principle", "死信队列隔离毒性消息，防止持续阻塞正常消费。");
            return payload;
        }, "RocketMQ 死信场景完成");
    }

    public ApiResponse<Object> idempotentDemo() {
        return executeScenario("RocketMQ 幂等场景失败", () -> {
            String messageKey = nextMessageKey("IDEMPOTENT");
            RocketMqDemoMessage message = newMessage(SCENARIO_IDEMPOTENT, messageKey, null, "ORDER_INVENTORY", null,
                    "ORDER_INVENTORY", null);

            producer.sendIdempotent(message);
            producer.sendIdempotent(message);
            assertOrThrow(runtimeState.awaitIdempotentReceives(messageKey, 2, timeout()), "等待重复消费证据超时");

            List<Map<String, Object>> consumeResults = List.of(
                    Map.of("messageKey", messageKey, "attempt", 1, "processed", true, "effect", "库存扣减"),
                    Map.of("messageKey", messageKey, "attempt", 2, "processed", false, "effect", "被幂等拦截"));

            Map<String, Object> payload = basePayload(SCENARIO_IDEMPOTENT, "幂等消费防止重复扣减");
            payload.put("topic", topics().getIdempotent());
            payload.put("tag", "ORDER_INVENTORY");
            payload.put("messageKey", messageKey);
            payload.put("idempotentKey", messageKey);
            payload.put("receiveCount", runtimeState.getIdempotentReceiveCount(messageKey));
            payload.put("consumeResults", consumeResults);
            payload.put("principle", "消费端通过业务唯一键做幂等，抵抗重复投递。");
            return payload;
        }, "RocketMQ 幂等场景完成");
    }

    public ApiResponse<Object> orderlyDemo() {
        return executeScenario("RocketMQ 顺序消息场景失败", () -> {
            String orderId = nextOrderId();
            List<String> expected = List.of("CREATED", "PAID", "SHIPPED");
            for (String event : expected) {
                RocketMqDemoMessage message = newMessage(SCENARIO_ORDERLY, nextMessageKey("ORDERLY"), orderId, event,
                        null, "ORDER_STATUS", null);
                producer.sendOrderly(message, orderId);
            }
            assertOrThrow(runtimeState.awaitOrderlyEvents(orderId, expected, timeout()), "等待顺序消费证据超时");

            List<Map<String, Object>> orderedEvents = new ArrayList<>();
            int index = 1;
            for (String event : runtimeState.getOrderlyEvents(orderId)) {
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("step", index++);
                detail.put("event", event);
                detail.put("shardingKey", orderId);
                orderedEvents.add(detail);
            }

            Map<String, Object> payload = basePayload(SCENARIO_ORDERLY, "订单状态顺序流转");
            payload.put("topic", topics().getOrderly());
            payload.put("tag", "ORDER_STATUS");
            payload.put("orderId", orderId);
            payload.put("orderedEvents", orderedEvents);
            payload.put("principle", "同一业务键路由到同一队列实现局部有序消费。");
            return payload;
        }, "RocketMQ 顺序消息场景完成");
    }

    public ApiResponse<Object> delayCloseDemo() {
        return executeScenario("RocketMQ 延迟消息场景失败", () -> {
            String orderId = nextOrderId();
            String messageKey = nextMessageKey("DELAY");
            Instant createdAt = Instant.now();
            Instant executeAt = createdAt.plus(10, ChronoUnit.SECONDS);

            RocketMqDemoMessage message = newMessage(SCENARIO_DELAY, messageKey, orderId, "ORDER_AUTO_CLOSE", null,
                    "ORDER_AUTO_CLOSE", null);
            producer.sendDelayClose(message);
            assertOrThrow(runtimeState.awaitDelayClosed(orderId, timeout()), "等待延迟关单执行超时");

            Map<String, Object> payload = basePayload(SCENARIO_DELAY, "超时未支付自动关单");
            payload.put("topic", topics().getDelayClose());
            payload.put("tag", "ORDER_AUTO_CLOSE");
            payload.put("orderId", orderId);
            payload.put("messageKey", messageKey);
            payload.put("delayLevel", "10s");
            payload.put("createdAt", createdAt.toString());
            payload.put("scheduledAt", executeAt.toString());
            payload.put("closeResult", "CLOSED");
            payload.put("principle", "延迟消息用于超时任务编排，减少轮询与定时扫描。");
            return payload;
        }, "RocketMQ 延迟消息场景完成");
    }

    public ApiResponse<Object> txSendDemo() {
        return executeScenario("RocketMQ 事务发送场景失败", () -> {
            String txId = "TX-" + sequence.incrementAndGet();
            String messageKey = nextMessageKey("TXSEND");
            RocketMqDemoMessage message = newMessage(SCENARIO_TX_SEND, messageKey, null, "ORDER_PAY_SUCCESS", txId,
                    "ORDER_PAY_SUCCESS", null);

            TransactionSendResult sendResult = producer.sendTransaction(message);
            assertOrThrow(runtimeState.awaitTxDelivered(txId, timeout()), "等待事务消息投递超时");

            Map<String, Object> payload = basePayload(SCENARIO_TX_SEND, "本地事务与事务消息发送");
            payload.put("topic", topics().getTx());
            payload.put("tag", "ORDER_PAY_SUCCESS");
            payload.put("transactionId", txId);
            payload.put("messageKey", messageKey);
            payload.put("halfMessageStatus", sendResult.getSendStatus().name());
            payload.put("localTransaction", "SUCCESS");
            payload.put("finalMessageStatus", runtimeState.getTxCheckState(txId));
            payload.put("principle", "事务消息通过半消息与回查保证本地事务和消息最终一致。");
            return payload;
        }, "RocketMQ 事务发送场景完成");
    }

    public ApiResponse<Object> txCheckDemo() {
        return executeScenario("RocketMQ 事务回查场景失败", () -> {
            String txId = "TX-CHECK-" + sequence.incrementAndGet();
            String messageKey = nextMessageKey("TXCHECK");
            RocketMqDemoMessage message = newMessage(SCENARIO_TX_CHECK, messageKey, null, "ORDER_PAY_SUCCESS", txId,
                    "ORDER_PAY_SUCCESS", null);

            producer.sendTransaction(message);
            assertOrThrow(runtimeState.awaitTxCheckState(txId, timeout()), "等待事务状态初始化超时");
            String beforeCheck = runtimeState.getTxCheckState(txId);
            assertOrThrow(runtimeState.awaitTxCheckState(txId, "ROLLBACK", timeout()), "等待事务回查完成超时");
            String afterCheck = runtimeState.getTxCheckState(txId);

            Map<String, Object> payload = basePayload(SCENARIO_TX_CHECK, "事务回查恢复一致性");
            payload.put("topic", topics().getTx());
            payload.put("transactionId", txId);
            payload.put("messageKey", messageKey);
            payload.put("beforeCheck", beforeCheck);
            payload.put("checkerDecision", "ROLLBACK");
            payload.put("afterCheck", afterCheck);
            payload.put("principle", "Broker 回查事务状态，避免生产端异常导致消息悬挂。");
            return payload;
        }, "RocketMQ 事务回查场景完成");
    }

    public ApiResponse<Object> tagFilterDemo() {
        return executeScenario("RocketMQ Tag 过滤场景失败", () -> {
            String inventoryKey = nextMessageKey("TAGINV");
            String marketingKey = nextMessageKey("TAGMKT");
            producer.sendTag(newMessage(SCENARIO_TAG, inventoryKey, null, "ORDER_SPLIT", null, "INVENTORY", null));
            producer.sendTag(newMessage(SCENARIO_TAG, marketingKey, null, "ORDER_SPLIT", null, "MARKETING", null));

            assertOrThrow(runtimeState.awaitTagDelivered(inventoryKey, groups().getTagInventory(), timeout()),
                    "等待库存 Tag 消费超时");
            assertOrThrow(runtimeState.awaitTagDelivered(marketingKey, groups().getTagMarketing(), timeout()),
                    "等待营销 Tag 消费超时");

            Map<String, Object> payload = basePayload(SCENARIO_TAG, "多下游按 Tag 过滤订阅");
            payload.put("topic", topics().getTag());
            payload.put("publishedMessages", List.of(
                    Map.of("messageKey", inventoryKey, "tag", "INVENTORY"),
                    Map.of("messageKey", marketingKey, "tag", "MARKETING")));
            payload.put("subscriptions", List.of(
                    Map.of("consumerGroup", groups().getTagInventory(), "filter", "INVENTORY"),
                    Map.of("consumerGroup", groups().getTagMarketing(), "filter", "MARKETING")));
            payload.put("principle", "Tag/Key 过滤支持一条业务主线驱动多个领域消费者。");
            return payload;
        }, "RocketMQ Tag 过滤场景完成");
    }

    public ApiResponse<Object> replayDlqDemo() {
        return executeScenario("RocketMQ 死信重放场景失败", () -> {
            Map<String, Object> dlqSource = ensureDlqSeed();
            String replayMessageKey = nextMessageKey("REPLAY");
            String replaySourceKey = String.valueOf(dlqSource.get("messageKey"));
            RocketMqDemoMessage replayMessage = newMessage(SCENARIO_REPLAY, replayMessageKey, nextOrderId(),
                    "ORDER_REPLAY", null, "ORDER_REPLAY", replaySourceKey);
            producer.sendReplay(replayMessage);
            assertOrThrow(runtimeState.awaitBasicDelivery(replayMessageKey, 2, timeout()), "等待死信重放消费证据超时");

            Map<String, Object> payload = basePayload(SCENARIO_REPLAY, "死信重放与人工补偿");
            payload.put("replayMessageKey", replayMessageKey);
            payload.put("replaySourceKey", replaySourceKey);
            payload.put("originTopic", dlqSource.get("originTopic"));
            payload.put("operator", "ops-demo-user");
            payload.put("replayResult", "SUCCESS");
            payload.put("principle", "通过死信重放与补偿流程形成可运维的故障恢复闭环。");
            return payload;
        }, "RocketMQ 死信重放场景完成");
    }

    private Map<String, Object> basePayload(String scenarioId, String businessContext) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("scenarioId", scenarioId);
        payload.put("businessContext", businessContext);
        payload.put("timestamp", Instant.now().toString());
        return payload;
    }

    private ApiResponse<Object> executeScenario(String failPrefix, Supplier<Map<String, Object>> scenario,
            String successMsg) {
        if (!properties.isStrictRealMode()) {
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR,
                    failPrefix + ": strict real mode disabled (demo.rocketmq.strict-real-mode=false)");
        }
        try {
            return ApiResponse.success(successMsg, scenario.get());
        } catch (Exception e) {
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, failPrefix + ": " + rootMessage(e));
        }
    }

    private void assertOrThrow(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? throwable.getClass().getSimpleName() : current.getMessage();
    }

    private RocketMqDemoMessage newMessage(String scenarioId, String messageKey, String orderId, String event,
            String transactionId, String tag, String replaySourceKey) {
        RocketMqDemoMessage message = new RocketMqDemoMessage();
        message.setScenarioId(scenarioId);
        message.setMessageKey(messageKey);
        message.setOrderId(orderId);
        message.setEvent(event);
        message.setTransactionId(transactionId);
        message.setTag(tag);
        message.setReplaySourceKey(replaySourceKey);
        return message;
    }

    private Map<String, Object> ensureDlqSeed() {
        return runtimeState.latestDlqMessage().orElseGet(() -> {
            String seedKey = nextMessageKey("DLQ-SEED");
            RocketMqDemoMessage seedMessage = newMessage(SCENARIO_REPLAY, seedKey, null, "ORDER_STOCK_RESERVE", null,
                    "ORDER_STOCK_RESERVE", null);
            producer.sendDlqTrigger(seedMessage);
            assertOrThrow(runtimeState.awaitDlqObserved(seedKey, timeout()), "等待重放种子死信超时");
            return runtimeState.findDlqMessage(seedKey).orElseThrow(() -> new IllegalStateException("未捕获到死信种子"));
        });
    }

    private Duration timeout() {
        return Duration.ofMillis(Math.max(properties.getAwaitTimeoutMs(), 1000));
    }

    private RocketMqDemoProperties.Topics topics() {
        return properties.getTopics();
    }

    private RocketMqDemoProperties.ConsumerGroups groups() {
        return properties.getConsumerGroups();
    }

    private String nextMessageKey(String prefix) {
        return prefix + "-" + sequence.incrementAndGet();
    }

    private String nextOrderId() {
        return "ORD-" + sequence.incrementAndGet();
    }

    private static RocketMqDemoProperties defaultProperties() {
        RocketMqDemoProperties properties = new RocketMqDemoProperties();
        RocketMqDemoProperties.Topics topics = new RocketMqDemoProperties.Topics();
        topics.setOrderEvents("demo.order.events");
        topics.setRetry("demo.order.retry");
        topics.setDlqTrigger("demo.order.dlq.trigger");
        topics.setIdempotent("demo.order.idempotent");
        topics.setOrderly("demo.order.orderly");
        topics.setDelayClose("demo.order.delay.close");
        topics.setTx("demo.order.tx");
        topics.setTag("demo.order.tag");
        topics.setDlqObserve("%DLQ%demo-order-dlq-consumer-group");
        properties.setTopics(topics);

        RocketMqDemoProperties.ConsumerGroups groups = new RocketMqDemoProperties.ConsumerGroups();
        groups.setBasicInventory("demo-order-basic-inventory-group");
        groups.setBasicMarketing("demo-order-basic-marketing-group");
        groups.setRetry("demo-order-retry-consumer-group");
        groups.setDlqTrigger("demo-order-dlq-consumer-group");
        groups.setDlqObserver("demo-order-dlq-observer-group");
        groups.setIdempotent("demo-order-idempotent-consumer-group");
        groups.setOrderly("demo-order-orderly-consumer-group");
        groups.setDelayClose("demo-order-delay-close-consumer-group");
        groups.setTagInventory("demo-order-tag-inventory-consumer-group");
        groups.setTagMarketing("demo-order-tag-marketing-consumer-group");
        groups.setTxConsumer("demo-order-tx-consumer-group");
        properties.setConsumerGroups(groups);
        return properties;
    }
}
