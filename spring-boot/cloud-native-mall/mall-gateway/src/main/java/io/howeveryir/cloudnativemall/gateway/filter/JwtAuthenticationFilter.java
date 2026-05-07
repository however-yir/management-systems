package io.howeveryir.cloudnativemall.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.common.security.config.JwtProperties;
import io.howeveryir.cloudnativemall.common.security.exception.JwtAuthenticationException;
import io.howeveryir.cloudnativemall.common.security.jwt.JwtTokenService;
import io.howeveryir.cloudnativemall.common.security.model.TokenUser;
import io.howeveryir.cloudnativemall.gateway.config.GatewayWhitelistProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_NAME = "X-Username";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final GatewayWhitelistProperties whitelistProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                   JwtProperties jwtProperties,
                                   GatewayWhitelistProperties whitelistProperties,
                                   ObjectMapper objectMapper) {
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.whitelistProperties = whitelistProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(jwtProperties.getHeader());

        try {
            String token = jwtTokenService.removePrefix(authHeader);
            TokenUser tokenUser = jwtTokenService.parseToken(token);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(HEADER_USER_ID, String.valueOf(tokenUser.getUserId()))
                    .header(HEADER_USER_NAME, tokenUser.getUsername())
                    .header(HEADER_USER_ROLES, String.join(",", tokenUser.getRoles()))
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (JwtAuthenticationException ex) {
            return unauthorized(exchange, ex.getMessage());
        }
    }

    private boolean isWhitelisted(String path) {
        if (whitelistProperties.getWhitelist() == null || whitelistProperties.getWhitelist().isEmpty()) {
            return false;
        }
        return whitelistProperties.getWhitelist().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(ApiResponse.fail(ErrorCode.UNAUTHORIZED, message));
        } catch (JsonProcessingException e) {
            body = "{\"success\":false,\"code\":\"UNAUTHORIZED\",\"message\":\"Unauthorized\"}".getBytes();
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
