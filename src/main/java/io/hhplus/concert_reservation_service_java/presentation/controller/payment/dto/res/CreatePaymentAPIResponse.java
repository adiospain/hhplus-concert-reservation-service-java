package io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.res;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import lombok.AllArgsConstructor;


public record CreatePaymentAPIResponse
  (
  int price,
  int pointAfter){

  public static CreatePaymentAPIResponse from(PaymentDomain payment) {
    return new CreatePaymentAPIResponse(payment.getPrice(), payment.getPointAfter());
  }
}
