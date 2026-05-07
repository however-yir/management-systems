package group.teachingmanagerbk.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * jwt令牌工具
 */
public class JwtUtil {

    private static final String JWT_SECRET_ENV = "TM_JWT_SECRET";
    private static final String JWT_PREVIOUS_SECRET_ENV = "TM_JWT_PREVIOUS_SECRET";
    private static final int MIN_SECRET_LEN = 32;

    /* 生成JWT令牌 */
    public static String genJWT(Map<String,Object> claims) {
        String secret = requireSecret(JWT_SECRET_ENV);
        Date deadline = new Date(System.currentTimeMillis() + 3*3600*1000);
        return Jwts.builder()
                    .signWith(SignatureAlgorithm.HS512,secret)
                    .setClaims(claims)
                    .setExpiration(deadline)
                    .compact();
    }

    /* 解析JWT令牌 */
    public static Map<String, Object> parseJWT(String jwt) throws RuntimeException {
        RuntimeException lastError = null;
        for (String secret : verificationSecrets()) {
            try {
                return Jwts.parser().setSigningKey(secret)
                        .parseClaimsJws(jwt)
                        .getBody();
            } catch (RuntimeException e) {
                lastError = e;
            }
        }
        throw lastError == null ? new RuntimeException("JWT parse failed") : lastError;
    }

    private static List<String> verificationSecrets() {
        List<String> secrets = new ArrayList<>();
        String activeSecret = requireSecret(JWT_SECRET_ENV);
        secrets.add(activeSecret);

        String previousSecret = normalizeSecret(System.getenv(JWT_PREVIOUS_SECRET_ENV));
        if (previousSecret != null) {
            validateSecretStrength(previousSecret, JWT_PREVIOUS_SECRET_ENV);
            if (!previousSecret.equals(activeSecret)) {
                secrets.add(previousSecret);
            }
        }
        return secrets;
    }

    private static String requireSecret(String envName) {
        String value = normalizeSecret(System.getenv(envName));
        if (value == null) {
            throw new IllegalStateException("Missing env var: " + envName);
        }
        validateSecretStrength(value, envName);
        return value;
    }

    private static String normalizeSecret(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void validateSecretStrength(String secret, String envName) {
        if (secret.length() < MIN_SECRET_LEN) {
            throw new IllegalStateException(envName + " must be at least " + MIN_SECRET_LEN + " characters");
        }
    }

}
