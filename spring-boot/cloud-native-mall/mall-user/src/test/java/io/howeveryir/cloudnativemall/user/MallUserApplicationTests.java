package io.howeveryir.cloudnativemall.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false",
        "MALL_BOOTSTRAP_ADMIN_PASSWORD=Mall@123",
        "MALL_BOOTSTRAP_ALICE_PASSWORD=Alice@123",
        "MALL_BOOTSTRAP_BOB_PASSWORD=Bob@123"
})
class MallUserApplicationTests {

    @Test
    void contextLoads() {
    }
}
