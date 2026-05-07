package io.howeveryir.cloudnativemall.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRateLimitConfig {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Bean
    public KeyResolver userOrIpKeyResolver() {
        return exchange -> {
            String tenantId = exchange.getRequest().getHeaders().getFirst(TENANT_HEADER);
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String principal;
            if (userId != null && !userId.isBlank()) {
                principal = "uid:" + userId;
            } else {
                String ip = exchange.getRequest().getRemoteAddress() == null
                        ? "unknown"
                        : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                principal = "ip:" + ip;
            }

            String key = (tenantId == null || tenantId.isBlank())
                    ? principal
                    : "tenant:" + tenantId + ":" + principal;
            return reactor.core.publisher.Mono.just(key);
        };
    }
}
