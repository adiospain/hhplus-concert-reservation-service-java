package io.hhplus.concert_reservation_service_java.domain.payment.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public PaymentDTO of(Payment savedPayment, Reservation reservation) {
    if (savedPayment == null){
      throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    return PaymentDTO.builder()
        .id(savedPayment.getId())
        .reservationId(reservation.getId())
        .price(reservation.getReservedPrice())
        .createdAt(savedPayment.getCreatedAt())
        .build();
  }
}
