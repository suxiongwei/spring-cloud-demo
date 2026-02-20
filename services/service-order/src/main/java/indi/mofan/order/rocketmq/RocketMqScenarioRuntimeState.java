package indi.mofan.order.rocketmq;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

@Component
public class RocketMqScenarioRuntimeState {
    private final Map<String, Set<String>> basicDeliveries = new ConcurrentHashMap<>();
    private final Map<String, List<Integer>> retryAttempts = new ConcurrentHashMap<>();
    private final Set<String> retrySucceededKeys = ConcurrentHashMap.newKeySet();
    private final Set<String> dlqObservedKeys = ConcurrentHashMap.newKeySet();
    private final Map<String, Map<String, Object>> dlqMessages = new ConcurrentHashMap<>();
    private final List<String> dlqObservedOrder = new CopyOnWriteArrayList<>();
    private final Map<String, AtomicInteger> idempotentReceiveCount = new ConcurrentHashMap<>();
    private final Set<String> idempotentProcessedKeys = ConcurrentHashMap.newKeySet();
    private final Map<String, List<String>> orderlyEvents = new ConcurrentHashMap<>();
    private final Set<String> delayClosedOrders = ConcurrentHashMap.newKeySet();
    private final Set<String> txDeliveredIds = ConcurrentHashMap.newKeySet();
    private final Map<String, String> txCheckStates = new ConcurrentHashMap<>();
    private final Set<String> txCheckCompleted = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<String>> tagDeliveries = new ConcurrentHashMap<>();

    public void recordBasicDelivery(String messageKey, String consumerGroup) {
        basicDeliveries.computeIfAbsent(messageKey, k -> ConcurrentHashMap.newKeySet()).add(consumerGroup);
    }

    public boolean awaitBasicDelivery(String messageKey, int expectedConsumerCount, Duration timeout) {
        return await(() -> basicDeliveries.getOrDefault(messageKey, Collections.emptySet()).size() >= expectedConsumerCount, timeout);
    }

    public List<String> getBasicDeliveries(String messageKey) {
        return new ArrayList<>(basicDeliveries.getOrDefault(messageKey, Collections.emptySet()));
    }

    public void recordRetryAttempt(String messageKey, int attempt) {
        retryAttempts.computeIfAbsent(messageKey, k -> new CopyOnWriteArrayList<>()).add(attempt);
    }

    public List<Integer> getRetryAttempts(String messageKey) {
        return new ArrayList<>(retryAttempts.getOrDefault(messageKey, List.of()));
    }

    public void markRetrySucceeded(String messageKey) {
        retrySucceededKeys.add(messageKey);
    }

    public boolean awaitRetrySucceeded(String messageKey, Duration timeout) {
        return await(() -> retrySucceededKeys.contains(messageKey), timeout);
    }

    public void markDlqObserved(String messageKey) {
        dlqObservedKeys.add(messageKey);
    }

    public void recordDlqObserved(String messageKey, String dlqTopic, String originTopic, String reason) {
        markDlqObserved(messageKey);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("messageKey", messageKey);
        detail.put("topic", dlqTopic);
        detail.put("originTopic", originTopic);
        detail.put("reason", reason);
        dlqMessages.put(messageKey, detail);
        dlqObservedOrder.add(messageKey);
    }

    public boolean awaitDlqObserved(String messageKey, Duration timeout) {
        return await(() -> dlqObservedKeys.contains(messageKey), timeout);
    }

    public Optional<Map<String, Object>> latestDlqMessage() {
        if (dlqObservedOrder.isEmpty()) {
            return Optional.empty();
        }
        String key = dlqObservedOrder.get(dlqObservedOrder.size() - 1);
        Map<String, Object> message = dlqMessages.get(key);
        return Optional.ofNullable(message == null ? null : new LinkedHashMap<>(message));
    }

    public Optional<Map<String, Object>> findDlqMessage(String messageKey) {
        Map<String, Object> detail = dlqMessages.get(messageKey);
        return Optional.ofNullable(detail == null ? null : new LinkedHashMap<>(detail));
    }

    public void recordIdempotentReceive(String messageKey) {
        idempotentReceiveCount.computeIfAbsent(messageKey, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public boolean markIdempotentProcessIfAbsent(String messageKey) {
        return idempotentProcessedKeys.add(messageKey);
    }

    public int getIdempotentReceiveCount(String messageKey) {
        return idempotentReceiveCount.getOrDefault(messageKey, new AtomicInteger(0)).get();
    }

    public boolean isIdempotentProcessed(String messageKey) {
        return idempotentProcessedKeys.contains(messageKey);
    }

    public boolean awaitIdempotentReceives(String messageKey, int expectedCount, Duration timeout) {
        return await(() -> getIdempotentReceiveCount(messageKey) >= expectedCount, timeout);
    }

    public void recordOrderlyEvent(String orderId, String event) {
        orderlyEvents.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>()).add(event);
    }

    public List<String> getOrderlyEvents(String orderId) {
        return new ArrayList<>(orderlyEvents.getOrDefault(orderId, List.of()));
    }

    public boolean awaitOrderlyEvents(String orderId, List<String> expected, Duration timeout) {
        return await(() -> getOrderlyEvents(orderId).equals(expected), timeout);
    }

    public void markDelayClosed(String orderId) {
        delayClosedOrders.add(orderId);
    }

    public boolean awaitDelayClosed(String orderId, Duration timeout) {
        return await(() -> delayClosedOrders.contains(orderId), timeout);
    }

    public void markTxDelivered(String transactionId) {
        txDeliveredIds.add(transactionId);
    }

    public boolean awaitTxDelivered(String transactionId, Duration timeout) {
        return await(() -> txDeliveredIds.contains(transactionId), timeout);
    }

    public void recordTxCheckState(String transactionId, String state) {
        txCheckStates.put(transactionId, state);
    }

    public void markTxCheckCompleted(String transactionId) {
        txCheckCompleted.add(transactionId);
    }

    public String getTxCheckState(String transactionId) {
        return txCheckStates.get(transactionId);
    }

    public boolean awaitTxCheckState(String transactionId, Duration timeout) {
        return await(() -> txCheckStates.containsKey(transactionId), timeout);
    }

    public boolean awaitTxCheckState(String transactionId, String expectedState, Duration timeout) {
        return await(() -> expectedState.equals(txCheckStates.get(transactionId)), timeout);
    }

    public boolean awaitTxCheckCompleted(String transactionId, Duration timeout) {
        return await(() -> txCheckCompleted.contains(transactionId), timeout);
    }

    public void recordTagDelivery(String messageKey, String consumerGroup) {
        tagDeliveries.computeIfAbsent(messageKey, k -> ConcurrentHashMap.newKeySet()).add(consumerGroup);
    }

    public boolean awaitTagDelivered(String messageKey, String consumerGroup, Duration timeout) {
        return await(() -> tagDeliveries.getOrDefault(messageKey, Collections.emptySet()).contains(consumerGroup), timeout);
    }

    private boolean await(BooleanSupplier supplier, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (supplier.getAsBoolean()) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return supplier.getAsBoolean();
    }
}
