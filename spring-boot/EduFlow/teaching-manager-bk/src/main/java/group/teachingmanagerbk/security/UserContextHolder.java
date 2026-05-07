package group.teachingmanagerbk.security;

import java.util.Map;

public final class UserContextHolder {
    private static final ThreadLocal<Map<String, Object>> CLAIMS_HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void setClaims(Map<String, Object> claims) {
        CLAIMS_HOLDER.set(claims);
    }

    public static Map<String, Object> getClaims() {
        return CLAIMS_HOLDER.get();
    }

    public static void clear() {
        CLAIMS_HOLDER.remove();
    }
}

