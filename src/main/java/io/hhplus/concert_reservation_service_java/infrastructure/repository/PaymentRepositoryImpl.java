package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.payment.Payment;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
  private final PaymentJpaRepository paymentRepository;


  @Override
  public Payment save(Payment payment) {
    return paymentRepository.save(payment);
  }
}
