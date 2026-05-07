package io.howeveryir.cloudnativemall.gateway.controller;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
public class GatewayProbeController {

    @GetMapping("/status")
    public ApiResponse<Map<String, String>> status() {
        return ApiResponse.ok(Map.of(
                "service", "mall-gateway",
                "status", "UP",
                "auth", "enabled",
                "rateLimit", "gateway-level"));
    }
}
