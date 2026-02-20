package indi.mofan.order.controller;

import indi.mofan.common.ApiResponse;
import indi.mofan.order.rocketmq.RocketMqScenarioRuntimeState;
import indi.mofan.order.support.ScenarioControlState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/demo/control")
@RequiredArgsConstructor
public class ScenarioControlController {

    private final ScenarioControlState scenarioControlState;
    private final RocketMqScenarioRuntimeState rocketMqScenarioRuntimeState;

    @PostMapping("/reset")
    public ApiResponse<Object> reset() {
        scenarioControlState.reset();
        rocketMqScenarioRuntimeState.resetAll();
        return ApiResponse.success("scenario control reset", Map.of(
                "resetApplied", true,
                "targets", new String[] { "failureInjection", "rocketmqRuntimeState" }
        ));
    }

    @PostMapping("/failure-injection")
    public ApiResponse<Object> setFailureInjection(
            @RequestParam("scenarioId") String scenarioId,
            @RequestParam(value = "enabled", defaultValue = "true") boolean enabled) {
        scenarioControlState.setFailureInjection(scenarioId, enabled);
        return ApiResponse.success("scenario failure injection updated", Map.of(
                "scenarioId", scenarioId,
                "enabled", scenarioControlState.isFailureInjectionEnabled(scenarioId)
        ));
    }
}
