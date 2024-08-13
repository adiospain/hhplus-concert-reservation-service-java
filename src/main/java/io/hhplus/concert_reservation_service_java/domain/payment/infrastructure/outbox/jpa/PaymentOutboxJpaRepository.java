package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {


}
