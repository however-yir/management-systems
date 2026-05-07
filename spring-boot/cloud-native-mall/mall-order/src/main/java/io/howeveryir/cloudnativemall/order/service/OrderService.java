package io.howeveryir.cloudnativemall.order.service;

import io.seata.spring.annotation.GlobalTransactional;
import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.BusinessException;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.order.client.PaymentClient;
import io.howeveryir.cloudnativemall.order.model.CreateOrderAndPayRequest;
import io.howeveryir.cloudnativemall.order.model.CreateOrderRequest;
import io.howeveryir.cloudnativemall.order.model.OrderInfo;
import io.howeveryir.cloudnativemall.order.model.OrderPayResult;
import io.howeveryir.cloudnativemall.order.model.PaymentConfirmRequest;
import io.howeveryir.cloudnativemall.order.persistence.OrderEntity;
import io.howeveryir.cloudnativemall.order.persistence.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final AtomicLong sequence = new AtomicLong(10000);
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public OrderInfo createOrder(CreateOrderRequest request) {
        String orderNo = generateOrderNo();
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setUserId(request.getUserId());
        entity.setProductId(request.getProductId());
        entity.setQuantity(request.getQuantity());
        entity.setStatus("CREATED");

        OrderEntity saved = orderRepository.save(entity);
        return toOrderInfo(saved);
    }

    @Transactional
    @GlobalTransactional(name = "create-order-and-pay", rollbackFor = Exception.class)
    public OrderPayResult createOrderAndPay(CreateOrderAndPayRequest request) {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(request.getUserId());
        createOrderRequest.setProductId(request.getProductId());
        createOrderRequest.setQuantity(request.getQuantity());

        OrderInfo orderInfo = createOrder(createOrderRequest);

        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest();
        paymentRequest.setOrderNo(orderInfo.getOrderNo());
        paymentRequest.setAmount(request.getAmount());
        paymentRequest.setChannel(request.getChannel());

        ApiResponse<Map<String, Object>> response = paymentClient.confirm(paymentRequest);
        if (response == null || !response.success()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "payment service failed");
        }

        String paymentNo = extractPaymentNo(response.data());
        OrderEntity orderEntity = orderRepository.findByOrderNo(orderInfo.getOrderNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "order not found after create"));
        orderEntity.setStatus("PAID");
        orderEntity.setPaymentNo(paymentNo);
        orderEntity.setUpdatedAt(LocalDateTime.now());

        OrderEntity updated = orderRepository.save(orderEntity);
        OrderPayResult result = new OrderPayResult();
        result.setOrder(toOrderInfo(updated));
        result.setPaymentNo(paymentNo);
        return result;
    }

    public OrderInfo findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo).map(this::toOrderInfo).orElse(null);
    }

    private String generateOrderNo() {
        String orderNo;
        do {
            orderNo = "OD" + System.currentTimeMillis() + sequence.incrementAndGet();
        } while (orderRepository.existsByOrderNo(orderNo));
        return orderNo;
    }

    private String extractPaymentNo(Map<String, Object> paymentData) {
        if (paymentData == null) {
            return null;
        }
        Object paymentNo = paymentData.get("paymentNo");
        return paymentNo == null ? null : String.valueOf(paymentNo);
    }

    private OrderInfo toOrderInfo(OrderEntity entity) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(entity.getOrderNo());
        orderInfo.setUserId(entity.getUserId());
        orderInfo.setProductId(entity.getProductId());
        orderInfo.setQuantity(entity.getQuantity());
        orderInfo.setStatus(entity.getStatus());
        orderInfo.setPaymentNo(entity.getPaymentNo());
        orderInfo.setCreatedAt(entity.getCreatedAt());
        orderInfo.setUpdatedAt(entity.getUpdatedAt());
        return orderInfo;
    }
}
