package io.howeveryir.cloudnativemall.order.controller;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.order.model.CreateOrderAndPayRequest;
import io.howeveryir.cloudnativemall.order.model.CreateOrderRequest;
import io.howeveryir.cloudnativemall.order.model.OrderInfo;
import io.howeveryir.cloudnativemall.order.model.OrderPayResult;
import io.howeveryir.cloudnativemall.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderInfo> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok("order created", orderService.createOrder(request));
    }

    @PostMapping("/checkout")
    public ApiResponse<OrderPayResult> checkout(@Valid @RequestBody CreateOrderAndPayRequest request) {
        return ApiResponse.ok("checkout success", orderService.createOrderAndPay(request));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<OrderInfo> detail(@PathVariable @Pattern(regexp = "^OD\\d+$", message = "orderNo format must be OD + digits") String orderNo) {
        OrderInfo order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            return ApiResponse.fail(ErrorCode.ORDER_NOT_FOUND, "order not found");
        }
        return ApiResponse.ok(order);
    }
}
