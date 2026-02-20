package indi.mofan.order.service.redis;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;

import java.lang.reflect.Method;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisScenarioServiceTest {

    @Test
    void shouldCalculateHitRate() throws Exception {
        RedisScenarioService service = new RedisScenarioService(null, null);
        RedisConnection connection = mock(RedisConnection.class);
        Properties stats = new Properties();
        stats.setProperty("keyspace_hits", "80");
        stats.setProperty("keyspace_misses", "20");
        when(connection.info("stats")).thenReturn(stats);

        Method method = RedisScenarioService.class.getDeclaredMethod("calculateHitRate", RedisConnection.class);
        method.setAccessible(true);
        String rate = (String) method.invoke(service, connection);

        assertThat(rate).isEqualTo("80.00%");
    }

    @Test
    void shouldReturnZeroPercentWhenNoStats() throws Exception {
        RedisScenarioService service = new RedisScenarioService(null, null);
        RedisConnection connection = mock(RedisConnection.class);
        Properties stats = new Properties();
        stats.setProperty("keyspace_hits", "0");
        stats.setProperty("keyspace_misses", "0");
        when(connection.info("stats")).thenReturn(stats);

        Method method = RedisScenarioService.class.getDeclaredMethod("calculateHitRate", RedisConnection.class);
        method.setAccessible(true);
        String rate = (String) method.invoke(service, connection);

        assertThat(rate).isEqualTo("0%");
    }
}
