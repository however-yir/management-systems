package group.teachingmanagerbk.security;

public final class RoleConstants {
    public static final String ROLE_ALL = "ROLE_ALL";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_STUDENT = "ROLE_STUDENT";
    public static final String ROLE_TEACHER = "ROLE_TEACHER";

    private RoleConstants() {
    }

    public static String fromLegacyCode(String code) {
        return switch (code) {
            case "1", ROLE_ADMIN -> ROLE_ADMIN;
            case "2", ROLE_STUDENT -> ROLE_STUDENT;
            case "3", ROLE_TEACHER -> ROLE_TEACHER;
            default -> code;
        };
    }
}

