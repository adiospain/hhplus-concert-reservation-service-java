package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
