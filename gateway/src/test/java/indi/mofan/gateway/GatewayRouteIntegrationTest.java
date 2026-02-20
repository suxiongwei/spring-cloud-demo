package indi.mofan.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = GatewayMainApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.nacos.discovery.enabled=false",
                "spring.cloud.nacos.config.enabled=false",
                "spring.cloud.loadbalancer.nacos.enabled=false"
        })
class GatewayRouteIntegrationTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldLoadContractCriticalRoutes() {
        List<Route> routes = routeLocator.getRoutes().collectList().block(Duration.ofSeconds(10));
        assertThat(routes).isNotNull();

        List<String> routeIds = routes.stream().map(Route::getId).toList();
        assertThat(routeIds).contains(
                "order-route",
                "product-route",
                "business-route",
                "order-dubbo-route",
                "legacy-flow-control",
                "legacy-seata-tcc-commit");
    }
}
