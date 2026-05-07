package io.howeveryir.cloudnativemall.common.security.jwt;

import io.howeveryir.cloudnativemall.common.security.config.JwtProperties;
import io.howeveryir.cloudnativemall.common.security.exception.JwtAuthenticationException;
import io.howeveryir.cloudnativemall.common.security.model.TokenUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class JwtTokenServiceTests {

    @Test
    void shouldGenerateAndParseToken() {
        JwtProperties jwtProperties = buildProperties();
        JwtTokenService service = new JwtTokenService(jwtProperties);

        TokenUser source = new TokenUser();
        source.setUserId(100L);
        source.setUsername("alice");
        source.setRoles(List.of("ROLE_USER"));

        String token = service.generateToken(source);
        TokenUser parsed = service.parseToken(token);

        Assertions.assertEquals(source.getUserId(), parsed.getUserId());
        Assertions.assertEquals(source.getUsername(), parsed.getUsername());
        Assertions.assertEquals(source.getRoles(), parsed.getRoles());
    }

    @Test
    void shouldRejectUnsupportedPrefix() {
        JwtProperties jwtProperties = buildProperties();
        JwtTokenService service = new JwtTokenService(jwtProperties);

        Assertions.assertThrows(JwtAuthenticationException.class, () -> service.removePrefix("Token abc"));
    }

    private JwtProperties buildProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("cloud-native-mall");
        jwtProperties.setSecret("change-this-to-a-very-long-secret-key-for-jwt-demo-2026");
        jwtProperties.setBase64Secret(false);
        jwtProperties.setAccessTokenExpireSeconds(7200);
        jwtProperties.setHeader("Authorization");
        jwtProperties.setPrefix("Bearer ");
        return jwtProperties;
    }
}
