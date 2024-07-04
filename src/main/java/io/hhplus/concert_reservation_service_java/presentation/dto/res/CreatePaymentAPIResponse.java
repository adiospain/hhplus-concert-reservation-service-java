package io.hhplus.concert_reservation_service_java.presentation.dto.res;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreatePaymentAPIResponse {
  private long paymentId;
  private int status;
  private int price;
  private int point;
}
