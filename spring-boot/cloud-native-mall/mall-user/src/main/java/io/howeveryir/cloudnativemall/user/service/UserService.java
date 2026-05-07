package io.howeveryir.cloudnativemall.user.service;

import io.howeveryir.cloudnativemall.common.security.model.TokenUser;
import io.howeveryir.cloudnativemall.user.model.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final Map<String, UserProfile> USER_PROFILES = Map.of(
            "mall-admin", new UserProfile(1L, "mall-admin", "system-admin", "13800000001"),
            "alice", new UserProfile(2L, "alice", "Alice", "13800000002"),
            "bob", new UserProfile(3L, "bob", "Bob", "13800000003"));

    private static final Map<String, List<String>> USER_ROLES = Map.of(
            "mall-admin", List.of("ROLE_ADMIN", "ROLE_USER"),
            "alice", List.of("ROLE_USER"),
            "bob", List.of("ROLE_USER"));

    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> encodedPasswords;

    public UserService(PasswordEncoder passwordEncoder,
                       @Value("${MALL_BOOTSTRAP_ADMIN_PASSWORD:}") String adminPassword,
                       @Value("${MALL_BOOTSTRAP_ALICE_PASSWORD:}") String alicePassword,
                       @Value("${MALL_BOOTSTRAP_BOB_PASSWORD:}") String bobPassword) {
        this.passwordEncoder = passwordEncoder;
        Map<String, String> rawPasswords = Map.of(
                "mall-admin", requireBootstrapSecret("MALL_BOOTSTRAP_ADMIN_PASSWORD", adminPassword),
                "alice", requireBootstrapSecret("MALL_BOOTSTRAP_ALICE_PASSWORD", alicePassword),
                "bob", requireBootstrapSecret("MALL_BOOTSTRAP_BOB_PASSWORD", bobPassword));
        this.encodedPasswords = rawPasswords.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> passwordEncoder.encode(entry.getValue())));
    }

    public TokenUser authenticate(String username, String password) {
        String encodedPassword = encodedPasswords.get(username);
        if (encodedPassword == null || !passwordEncoder.matches(password, encodedPassword)) {
            return null;
        }
        UserProfile profile = USER_PROFILES.get(username);
        if (profile == null) {
            return null;
        }
        TokenUser tokenUser = new TokenUser();
        tokenUser.setUserId(profile.getId());
        tokenUser.setUsername(profile.getUsername());
        tokenUser.setRoles(USER_ROLES.getOrDefault(profile.getUsername(), List.of("ROLE_USER")));
        return tokenUser;
    }

    public UserProfile findById(Long id) {
        return USER_PROFILES.values().stream()
                .filter(profile -> profile.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private String requireBootstrapSecret(String key, String value) {
        if (value == null) {
            throw new IllegalStateException("Missing bootstrap secret env: " + key);
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("change_me_")) {
            throw new IllegalStateException("Invalid bootstrap secret env: " + key);
        }
        return trimmed;
    }
}
