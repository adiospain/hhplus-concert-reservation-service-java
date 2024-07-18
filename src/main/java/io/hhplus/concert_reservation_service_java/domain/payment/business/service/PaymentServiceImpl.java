package io.hhplus.concert_reservation_service_java.domain.payment.business.service;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;

  @Override
  @Transactional
  public Payment save(Payment payment) {
    if (payment == null){
      throw new CustomException(ErrorCode.OBJECT_CANNOT_BE_NULL);
    }
    return paymentRepository.save(payment);
  }

  @Override
  public Payment createPayment(long reserverId, long reservationId) {
    Payment savedPayment = Payment.createFrom(reserverId, reservationId);
    paymentRepository.save(savedPayment);
    return savedPayment;
  }
}
