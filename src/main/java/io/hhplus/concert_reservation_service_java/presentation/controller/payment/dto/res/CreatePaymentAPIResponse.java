package io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.res;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreatePaymentAPIResponse {
  private long paymentId;
  private int price;
  private int pointAfter;

  public static CreatePaymentAPIResponse from(PaymentDomain payment) {
    return new CreatePaymentAPIResponse(payment.getId(), payment.getPrice(), payment.getPointAfter());
  }
}
