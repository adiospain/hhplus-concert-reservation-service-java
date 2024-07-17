package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;

public interface PaymentService {

  Payment save(Payment payment);

  Payment createPayment(long userId, long reservationId);
}
