package io.hhplus.concert_reservation_service_java.domain.user.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public PaymentDomain of(Payment savedPayment, Reservation reservation) {
    if (savedPayment == null){
      throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    return PaymentDomain.builder()
        .id(savedPayment.getId())
        .reservationId(reservation.getId())
        .price(reservation.getReservedPrice())
        .createdAt(savedPayment.getCreatedAt())
        .build();
  }

  public PaymentDomain of(Payment savedPayment, Reservation reservation, User reserver) {
    if (savedPayment == null){
      throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    return PaymentDomain.builder()
        .id(savedPayment.getId())
        .reservationId(reservation.getId())
        .price(reservation.getReservedPrice())
        .pointAfter(reserver.getPoint())
        .createdAt(savedPayment.getCreatedAt())
        .build();
  }

  public List<PaymentDomain> from (List<Payment> payments){
    if (payments == null || payments.isEmpty()){
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }
    return payments.stream()
        .map(this::convertToPaymentDomain)
        .collect(Collectors.toList());
  }

  private PaymentDomain convertToPaymentDomain(Payment payment) {
    return PaymentDomain.builder()
        .id(payment.getId())
        .reservationId(payment.getReservationId())
        .price(payment.getReservedPrice())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
