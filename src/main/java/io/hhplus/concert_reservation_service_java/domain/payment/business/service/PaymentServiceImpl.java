package io.hhplus.concert_reservation_service_java.domain.payment.business.service;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
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
  public Payment createPayment(long reserverId, Reservation reservation) {
    Payment savedPayment = Payment.createFrom(reserverId, reservation.getId(), reservation.getReservedPrice());
    paymentRepository.save(savedPayment);
    return savedPayment;
  }
}
