package indi.mofan.business.feign.fallback;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeignFallbackTest {

    @Test
    void accountFallbackShouldThrowForAllOperations() {
        AccountFeignFallback fallback = new AccountFeignFallback();

        assertThatThrownBy(() -> fallback.debit("u1", 100))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("account service unavailable");
        assertThatThrownBy(() -> fallback.addBack("u1", 100))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("account service unavailable");
        assertThatThrownBy(() -> fallback.snapshot("u1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("account service unavailable");
    }

    @Test
    void storageFallbackShouldThrowForAllOperations() {
        StorageFeignFallback fallback = new StorageFeignFallback();

        assertThatThrownBy(() -> fallback.deduct("c1", 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("storage service unavailable");
        assertThatThrownBy(() -> fallback.addBack("c1", 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("storage service unavailable");
        assertThatThrownBy(() -> fallback.snapshot("c1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("storage service unavailable");
    }

    @Test
    void orderFallbackShouldThrowForAllOperations() {
        OrderFeignFallback fallback = new OrderFeignFallback();

        assertThatThrownBy(() -> fallback.create("u1", "c1", 1, false))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("order service unavailable");
        assertThatThrownBy(() -> fallback.snapshot("u1", "c1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("order service unavailable");
    }
}
