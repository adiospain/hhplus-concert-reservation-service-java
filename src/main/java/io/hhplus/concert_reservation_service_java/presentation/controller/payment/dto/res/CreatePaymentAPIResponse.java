package io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreatePaymentAPIResponse {
  private long paymentId;
  private int status;
  private int price;
  private int point;

  public static CreatePaymentAPIResponse from(PaymentDTO payment) {
    return new CreatePaymentAPIResponse(2,2,2,1);
  }
}
