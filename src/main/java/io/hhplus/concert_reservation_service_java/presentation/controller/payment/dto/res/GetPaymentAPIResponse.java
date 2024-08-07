package io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.res;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetPaymentAPIResponse {
  List<PaymentDomain> payments;

  public static GetPaymentAPIResponse from(List<PaymentDomain> payments) {
    return new GetPaymentAPIResponse(payments);
  }
}
