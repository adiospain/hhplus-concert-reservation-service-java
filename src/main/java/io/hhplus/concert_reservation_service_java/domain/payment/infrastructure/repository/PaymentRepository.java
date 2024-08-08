package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import java.util.List;

public interface PaymentRepository {

  Payment save(Payment payment);

  Payment findById(long paymentId);

  List<Payment> findByUserId(long userId);
}
