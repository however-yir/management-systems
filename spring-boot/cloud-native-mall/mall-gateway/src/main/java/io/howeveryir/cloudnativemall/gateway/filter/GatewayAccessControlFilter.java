package io.howeveryir.cloudnativemall.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.gateway.config.GatewayAccessControlProperties;
import io.howeveryir.cloudnativemall.gateway.config.GatewayWhitelistProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GatewayAccessControlFilter implements GlobalFilter, Ordered {

    private final GatewayWhitelistProperties whitelistProperties;
    private final GatewayAccessControlProperties accessControlProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public GatewayAccessControlFilter(GatewayWhitelistProperties whitelistProperties,
                                      GatewayAccessControlProperties accessControlProperties,
                                      ObjectMapper objectMapper) {
        this.whitelistProperties = whitelistProperties;
        this.accessControlProperties = accessControlProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String clientIp = resolveClientIp(exchange);
        if (accessControlProperties.getIpBlacklist().contains(clientIp)) {
            return forbidden(exchange, "client ip is blocked");
        }

        String tenantId = exchange.getRequest().getHeaders().getFirst(accessControlProperties.getTenantHeader());
        if (tenantId == null || tenantId.isBlank()) {
            return forbidden(exchange, "missing tenant header");
        }
        if (accessControlProperties.getTenantBlacklist().contains(tenantId)) {
            return forbidden(exchange, "tenant is blocked");
        }
        if (!accessControlProperties.getTenantWhitelist().isEmpty()
                && !accessControlProperties.getTenantWhitelist().contains(tenantId)) {
            return forbidden(exchange, "tenant is not allowed");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }

    private boolean isWhitelisted(String path) {
        if (whitelistProperties.getWhitelist() == null || whitelistProperties.getWhitelist().isEmpty()) {
            return false;
        }
        return whitelistProperties.getWhitelist().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private String resolveClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        if (exchange.getRequest().getRemoteAddress() == null || exchange.getRequest().getRemoteAddress().getAddress() == null) {
            return "unknown";
        }
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(ApiResponse.fail(ErrorCode.FORBIDDEN, message));
        } catch (JsonProcessingException e) {
            body = "{\"success\":false,\"code\":\"FORBIDDEN\",\"message\":\"forbidden\"}"
                    .getBytes(StandardCharsets.UTF_8);
        }
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }
}
