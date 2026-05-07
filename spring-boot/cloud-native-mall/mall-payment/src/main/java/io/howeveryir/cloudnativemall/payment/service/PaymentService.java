package io.howeveryir.cloudnativemall.payment.service;

import io.howeveryir.cloudnativemall.payment.model.PayRequest;
import io.howeveryir.cloudnativemall.payment.model.PaymentInfo;
import io.howeveryir.cloudnativemall.payment.persistence.PaymentEntity;
import io.howeveryir.cloudnativemall.payment.persistence.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private final AtomicLong sequence = new AtomicLong(90000);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentInfo pay(PayRequest request) {
        PaymentEntity existed = paymentRepository.findByOrderNo(request.getOrderNo()).orElse(null);
        if (existed != null) {
            return toPaymentInfo(existed);
        }

        PaymentEntity entity = new PaymentEntity();
        entity.setPaymentNo(generatePaymentNo());
        entity.setOrderNo(request.getOrderNo());
        entity.setAmount(request.getAmount());
        entity.setChannel(request.getChannel());
        entity.setStatus("PAID");
        entity.setPaidAt(LocalDateTime.now());

        return toPaymentInfo(paymentRepository.save(entity));
    }

    private String generatePaymentNo() {
        String paymentNo;
        do {
            paymentNo = "PM" + System.currentTimeMillis() + sequence.incrementAndGet();
        } while (paymentRepository.existsByPaymentNo(paymentNo));
        return paymentNo;
    }

    private PaymentInfo toPaymentInfo(PaymentEntity entity) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentNo(entity.getPaymentNo());
        paymentInfo.setOrderNo(entity.getOrderNo());
        paymentInfo.setAmount(entity.getAmount());
        paymentInfo.setChannel(entity.getChannel());
        paymentInfo.setStatus(entity.getStatus());
        paymentInfo.setPaidAt(entity.getPaidAt());
        return paymentInfo;
    }
}
