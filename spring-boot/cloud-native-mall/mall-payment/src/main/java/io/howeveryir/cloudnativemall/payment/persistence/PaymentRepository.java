package io.howeveryir.cloudnativemall.payment.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderNo(String orderNo);

    boolean existsByPaymentNo(String paymentNo);
}
