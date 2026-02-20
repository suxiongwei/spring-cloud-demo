package indi.mofan.order.controller;

import indi.mofan.order.support.ScenarioControlState;
import indi.mofan.order.rocketmq.RocketMqScenarioRuntimeState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScenarioControlController.class)
class ScenarioControlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScenarioControlState scenarioControlState;

    @MockBean
    private RocketMqScenarioRuntimeState rocketMqScenarioRuntimeState;

    @Test
    void shouldResetScenarioState() throws Exception {
        doNothing().when(scenarioControlState).reset();
        doNothing().when(rocketMqScenarioRuntimeState).resetAll();

        mockMvc.perform(post("/demo/control/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.resetApplied").value(true));
    }

    @Test
    void shouldSetFailureInjectionFlag() throws Exception {
        when(scenarioControlState.isFailureInjectionEnabled("rmq-01")).thenReturn(true);

        mockMvc.perform(post("/demo/control/failure-injection")
                        .param("scenarioId", "rmq-01")
                        .param("enabled", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.scenarioId").value("rmq-01"))
                .andExpect(jsonPath("$.data.enabled").value(true));
    }
}
