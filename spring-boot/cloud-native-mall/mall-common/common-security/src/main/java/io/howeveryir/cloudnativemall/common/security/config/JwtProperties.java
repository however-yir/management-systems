package io.howeveryir.cloudnativemall.common.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "security.jwt")
@Validated
public class JwtProperties {

    @NotBlank
    private String secret;
    private boolean base64Secret = false;
    @NotBlank
    private String issuer = "cloud-native-mall";
    @Min(60)
    private long accessTokenExpireSeconds = 7200;
    @NotBlank
    private String header = "Authorization";
    @NotBlank
    private String prefix = "Bearer ";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isBase64Secret() {
        return base64Secret;
    }

    public void setBase64Secret(boolean base64Secret) {
        this.base64Secret = base64Secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getAccessTokenExpireSeconds() {
        return accessTokenExpireSeconds;
    }

    public void setAccessTokenExpireSeconds(long accessTokenExpireSeconds) {
        this.accessTokenExpireSeconds = accessTokenExpireSeconds;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
