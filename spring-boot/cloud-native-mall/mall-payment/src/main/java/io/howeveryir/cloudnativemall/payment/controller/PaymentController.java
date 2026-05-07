package io.howeveryir.cloudnativemall.payment.controller;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.payment.model.PayRequest;
import io.howeveryir.cloudnativemall.payment.model.PaymentInfo;
import io.howeveryir.cloudnativemall.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/confirm")
    public ApiResponse<PaymentInfo> confirm(@Valid @RequestBody PayRequest request) {
        return ApiResponse.ok("payment success", paymentService.pay(request));
    }
}
