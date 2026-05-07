package io.howeveryir.cloudnativemall.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false"
})
class MallProductApplicationTests {

    @Test
    void contextLoads() {
    }
}
