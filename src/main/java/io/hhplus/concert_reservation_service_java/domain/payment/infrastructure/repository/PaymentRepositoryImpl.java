package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.PaymentJpaRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.util.List;
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

  @Override
  public Payment findById(long paymentId) {
    return paymentRepository.findById(paymentId)
        .orElseThrow(()->new CustomException(ErrorCode.UNSPECIFIED_FAIL));
  }

  @Override
  public List<Payment> findByUserId(long userId) {
    return paymentRepository.findByUserId(userId);
  }
}
