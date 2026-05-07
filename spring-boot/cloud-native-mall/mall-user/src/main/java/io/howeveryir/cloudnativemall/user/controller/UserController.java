package io.howeveryir.cloudnativemall.user.controller;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.user.model.UserProfile;
import io.howeveryir.cloudnativemall.user.service.UserService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserProfile> getById(@PathVariable @Positive(message = "id must be positive") Long id) {
        UserProfile profile = userService.findById(id);
        if (profile == null) {
            return ApiResponse.fail(ErrorCode.USER_NOT_FOUND, "user not found");
        }
        return ApiResponse.ok(profile);
    }
}
