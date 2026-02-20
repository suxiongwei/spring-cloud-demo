package indi.mofan.order.support;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScenarioControlState {

    private final Map<String, Boolean> failureInjectionFlags = new ConcurrentHashMap<>();

    public void setFailureInjection(String scenarioId, boolean enabled) {
        if (scenarioId == null || scenarioId.isBlank()) {
            return;
        }
        failureInjectionFlags.put(scenarioId, enabled);
    }

    public boolean isFailureInjectionEnabled(String scenarioId) {
        return failureInjectionFlags.getOrDefault(scenarioId, false);
    }

    public void reset() {
        failureInjectionFlags.clear();
    }
}
