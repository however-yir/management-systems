package io.howeveryir.cloudnativemall.common.core;

import java.time.Instant;

public record ApiResponse<T>(boolean success, String code, String message, T data, Instant timestamp) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", "success", data, Instant.now());
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, "OK", message, data, Instant.now());
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null, Instant.now());
    }

    public static <T> ApiResponse<T> fail(ErrorCode code, String message) {
        return fail(code.name(), message);
    }
}
