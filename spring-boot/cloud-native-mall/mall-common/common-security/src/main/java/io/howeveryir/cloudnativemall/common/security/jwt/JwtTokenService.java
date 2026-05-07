package io.howeveryir.cloudnativemall.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.howeveryir.cloudnativemall.common.security.config.JwtProperties;
import io.howeveryir.cloudnativemall.common.security.exception.JwtAuthenticationException;
import io.howeveryir.cloudnativemall.common.security.model.TokenUser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JwtTokenService {

    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(TokenUser tokenUser) {
        Objects.requireNonNull(tokenUser, "tokenUser must not be null");
        Objects.requireNonNull(tokenUser.getUserId(), "userId must not be null");
        Objects.requireNonNull(tokenUser.getUsername(), "username must not be null");
        List<String> roles = tokenUser.getRoles() == null ? List.of() : tokenUser.getRoles();

        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(jwtProperties.getAccessTokenExpireSeconds());

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .claims(Map.of(
                        CLAIM_UID, tokenUser.getUserId(),
                        CLAIM_USERNAME, tokenUser.getUsername(),
                        CLAIM_ROLES, roles))
                .signWith(resolveKey())
                .compact();
    }

    @SuppressWarnings("unchecked")
    public TokenUser parseToken(String rawToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(resolveKey())
                    .build()
                    .parseSignedClaims(rawToken)
                    .getPayload();

            TokenUser tokenUser = new TokenUser();
            validateIssuer(claims);
            tokenUser.setUserId(Long.parseLong(String.valueOf(claims.get(CLAIM_UID))));
            tokenUser.setUsername(String.valueOf(claims.get(CLAIM_USERNAME)));
            tokenUser.setRoles(parseRoles(claims.get(CLAIM_ROLES)));
            return tokenUser;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtAuthenticationException("Invalid or expired token", ex);
        }
    }

    public String removePrefix(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new JwtAuthenticationException("Missing authorization header");
        }
        String prefix = jwtProperties.getPrefix();
        if (!authorizationHeader.startsWith(prefix)) {
            throw new JwtAuthenticationException("Unsupported token prefix");
        }
        return authorizationHeader.substring(prefix.length());
    }

    private SecretKey resolveKey() {
        String secret = jwtProperties.getSecret();
        byte[] keyBytes;
        if (jwtProperties.isBase64Secret()) {
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new JwtAuthenticationException("JWT secret is too short, must be at least 32 bytes");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void validateIssuer(Claims claims) {
        if (!Objects.equals(jwtProperties.getIssuer(), claims.getIssuer())) {
            throw new JwtAuthenticationException("Invalid token issuer");
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> parseRoles(Object rolesClaim) {
        if (rolesClaim == null) {
            return List.of();
        }
        if (!(rolesClaim instanceof List<?> roles)) {
            throw new JwtAuthenticationException("Invalid roles claim");
        }
        return roles.stream().map(String::valueOf).toList();
    }
}
