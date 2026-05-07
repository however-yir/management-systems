package io.howeveryir.cloudnativemall.order;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.order.client.PaymentClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false",
        "seata.enabled=false"
})
@AutoConfigureMockMvc
class OrderOpenApiContractTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentClient paymentClient;

    @Test
    void shouldExposeOrderApiInOpenApiDocument() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/orders']").exists())
                .andExpect(jsonPath("$.paths['/api/orders/checkout']").exists())
                .andExpect(jsonPath("$.paths['/api/orders/{orderNo}']").exists());
    }

    @Test
    void checkoutContractShouldReturnOrderAndPaymentNo() throws Exception {
        given(paymentClient.confirm(any())).willReturn(ApiResponse.ok(Map.of("paymentNo", "PM100001")));

        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"productId\":1001,\"quantity\":1,\"amount\":399.00,\"channel\":\"WECHAT_PAY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.order.orderNo").isNotEmpty())
                .andExpect(jsonPath("$.data.order.status").value("PAID"))
                .andExpect(jsonPath("$.data.paymentNo").value("PM100001"));
    }
}
