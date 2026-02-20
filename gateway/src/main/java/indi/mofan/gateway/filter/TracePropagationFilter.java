package indi.mofan.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Slf4j
@Component
public class TracePropagationFilter implements GlobalFilter, Ordered {
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${demo.tracing.enabled:false}")
    private boolean tracingEnabled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!tracingEnabled) {
            return chain.filter(exchange);
        }

        String incoming = exchange.getRequest().getHeaders().getFirst("traceparent");
        String traceparent = (incoming == null || incoming.isBlank()) ? buildTraceparent() : incoming;

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("traceparent", traceparent)
                .build();

        exchange.getResponse().getHeaders().set("traceparent", traceparent);
        log.debug("propagate traceparent={}", traceparent);
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -200;
    }

    private String buildTraceparent() {
        return "00-" + randomHex(32) + "-" + randomHex(16) + "-01";
    }

    private String randomHex(int len) {
        byte[] bytes = new byte[len / 2];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(len);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
