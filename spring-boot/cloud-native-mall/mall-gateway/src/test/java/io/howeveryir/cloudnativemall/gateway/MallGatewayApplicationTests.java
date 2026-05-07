package io.howeveryir.cloudnativemall.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false",
        "security.jwt.secret=change-this-to-a-very-long-secret-key-for-gateway-tests-2026"
})
class MallGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
