package io.howeveryir.cloudnativemall.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false",
        "seata.enabled=false"
})
class MallPaymentApplicationTests {

    @Test
    void contextLoads() {
    }
}
