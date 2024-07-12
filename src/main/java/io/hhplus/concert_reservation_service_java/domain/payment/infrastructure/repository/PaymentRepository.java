package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;

public interface PaymentRepository {

  Payment save(Payment payment);
}
