package io.howeveryir.cloudnativemall.user.controller;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.common.security.config.JwtProperties;
import io.howeveryir.cloudnativemall.common.security.jwt.JwtTokenService;
import io.howeveryir.cloudnativemall.common.security.model.TokenUser;
import io.howeveryir.cloudnativemall.user.model.LoginRequest;
import io.howeveryir.cloudnativemall.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;

    public AuthController(UserService userService, JwtTokenService jwtTokenService, JwtProperties jwtProperties) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        TokenUser tokenUser = userService.authenticate(request.getUsername(), request.getPassword());
        if (tokenUser == null) {
            return ApiResponse.fail(ErrorCode.INVALID_CREDENTIALS, "invalid username or password");
        }

        String token = jwtTokenService.generateToken(tokenUser);
        return ApiResponse.ok("login success", Map.of(
                "accessToken", token,
                "tokenType", "Bearer",
                "expiresIn", String.valueOf(jwtProperties.getAccessTokenExpireSeconds()),
                "roles", String.join(",", tokenUser.getRoles())));
    }
}
