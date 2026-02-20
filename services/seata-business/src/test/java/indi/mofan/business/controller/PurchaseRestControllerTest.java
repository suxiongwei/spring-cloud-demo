package indi.mofan.business.controller;

import indi.mofan.business.feign.AccountFeignClient;
import indi.mofan.business.feign.OrderFeignClient;
import indi.mofan.business.feign.StorageFeignClient;
import indi.mofan.business.service.BusinessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseRestController.class)
class PurchaseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;
    @MockBean
    private AccountFeignClient accountFeignClient;
    @MockBean
    private StorageFeignClient storageFeignClient;
    @MockBean
    private OrderFeignClient orderFeignClient;

    @Test
    void shouldVerifyCommitWhenPurchaseTccSuccess() throws Exception {
        when(accountFeignClient.snapshot("u1"))
                .thenReturn(Map.of("money", 100), Map.of("money", 91));
        when(storageFeignClient.snapshot("c1"))
                .thenReturn(Map.of("count", 50), Map.of("count", 49));
        when(orderFeignClient.snapshot("u1", "c1"))
                .thenReturn(Map.of("orderCount", 2), Map.of("orderCount", 3));
        doNothing().when(businessService).purchaseTcc("u1", "c1", 1, false);

        mockMvc.perform(get("/purchase/tcc/verify")
                        .param("userId", "u1")
                        .param("commodityCode", "c1")
                        .param("count", "1")
                        .param("fail", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.verification.commitVerified").value(true))
                .andExpect(jsonPath("$.verification.rollbackVerified").value(false));
    }

    @Test
    void shouldVerifyRollbackWhenPurchaseTccFails() throws Exception {
        when(accountFeignClient.snapshot("u1"))
                .thenReturn(Map.of("money", 100), Map.of("money", 100));
        when(storageFeignClient.snapshot("c1"))
                .thenReturn(Map.of("count", 50), Map.of("count", 50));
        when(orderFeignClient.snapshot("u1", "c1"))
                .thenReturn(Map.of("orderCount", 2), Map.of("orderCount", 2));
        doThrow(new RuntimeException("mock tcc fail"))
                .when(businessService).purchaseTcc("u1", "c1", 1, true);

        mockMvc.perform(get("/purchase/tcc/verify")
                        .param("userId", "u1")
                        .param("commodityCode", "c1")
                        .param("count", "1")
                        .param("fail", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.verification.commitVerified").value(false))
                .andExpect(jsonPath("$.verification.rollbackVerified").value(true));
    }

    @Test
    void shouldToggleGlobalFailureInjection() throws Exception {
        mockMvc.perform(post("/purchase/tcc/control/failure-injection").param("enabled", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(true))
                .andExpect(jsonPath("$.globalFailInjection").value(true));

        mockMvc.perform(post("/purchase/tcc/control/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetApplied").value(true))
                .andExpect(jsonPath("$.globalFailInjection").value(false));
    }
}
