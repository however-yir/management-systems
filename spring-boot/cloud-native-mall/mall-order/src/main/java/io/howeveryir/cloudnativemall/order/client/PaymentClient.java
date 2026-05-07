package io.howeveryir.cloudnativemall.order.client;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.order.model.PaymentConfirmRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "mall-payment", url = "${PAYMENT_SERVICE_URI:}")
public interface PaymentClient {

    @PostMapping("/api/payments/confirm")
    ApiResponse<Map<String, Object>> confirm(@RequestBody PaymentConfirmRequest request);
}
