package io.hhplus.concert_reservation_service_java.application.payment.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePaymentCommand {
  long reserverId;
  long reservationId;
}
